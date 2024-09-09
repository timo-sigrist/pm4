'use client';

import Title1 from "@/components/title1";

import { ResponsiveChartContainer } from '@mui/x-charts/ResponsiveChartContainer';
import { LinePlot, MarkPlot } from '@mui/x-charts/LineChart';
import { BarPlot } from '@mui/x-charts/BarChart';
import { ChartsXAxis } from '@mui/x-charts/ChartsXAxis';
import { ChartsYAxis } from '@mui/x-charts/ChartsYAxis';
import { ChartsGrid } from '@mui/x-charts/ChartsGrid';
import { ChartsTooltip } from '@mui/x-charts/ChartsTooltip';
import { LineSeriesType, type AllSeriesType, type BarSeriesType } from '@mui/x-charts';
import type { DatasetType } from "node_modules/@mui/x-charts/models/seriesType/config";
import chroma from 'chroma-js';
import { useEffect, useState } from "react";
import Select from "@/components/select";
import { getDaySheetControllerApi, getUserControllerApi } from "@/openapi/connector";
import { RatingDtoRatingRoleEnum, type UserDto } from "@/openapi/compassClient";

import ReportGenerator from "@/components/reportgenerator";

enum categorySelections {
  PARTICIPANT = "PARTICIPANT",
  SOCIAL_WORKER = "SOCIAL_WORKER",
  ALL = "ALL"
}

const getNextColor = (baseColor: string, level: number) => {
  return chroma.hex(baseColor).darken(level * 0.5).hex();
}

export default function MonthlyOverviewPage() {
  const [categorySelection, setCategorySelection] = useState<any>(categorySelections.PARTICIPANT);
  const [participantId, setParticipantId] = useState<string>();
  const [month, setMonth] = useState<string>(new Date().toLocaleString('en-US', { month: '2-digit' }).padStart(2, '0'));
  const [year, setYear] = useState<string>(new Date().getFullYear().toString());

  const [categories, setCategories] = useState<{ id: string, label: string }[]>([]);
  const [participants, setParticipants] = useState<{ id: string, label: string }[]>([]);
  const [months, setMonths] = useState<{ id: string, label: string }[]>([]);
  const [years, setYears] = useState<{ id: string, label: string }[]>([]);

  const [incidentsSeries, setIncidentsSeries] = useState<BarSeriesType[]>([]);
  const [dataSeries, setDataSeries] = useState<AllSeriesType[]>([]);

  const [incidentCountPerDay, setIncidentCountPerDay] = useState<DatasetType>([]);
  const [dataset, setDataset] = useState<DatasetType>([]);

  const [daySheets, setDaySheets] = useState<any[]>([]);
  const [participantDtos, setParticipantDtos] = useState<UserDto[]>([]);
  const [selectedParticipant, setSelectedParticipant] = useState<UserDto>();

  enum monthLabels {
    JANUARY = "Januar",
    FEBRUARY = "Februar",
    MARCH = "März",
    APRIL = "April",
    MAY = "Mai",
    JUNE = "Juni",
    JULY = "Juli",
    AUGUST = "August",
    SEPTEMBER = "September",
    OCTOBER = "Oktober",
    NOVEMBER = "November",
    DECEMBER = "Dezember"
  }

  useEffect(() => {
    setCategories([
      { id: categorySelections.PARTICIPANT, label: 'Teilnehmerkategorien' },
      { id: categorySelections.SOCIAL_WORKER, label: 'Sozialarbeiterkategorien' },
      { id: categorySelections.ALL, label: 'Alle Kategorien' }
    ])

    setMonths(Object.keys(monthLabels).map((key, index) => {
      const obj = {
        id: (index + 1).toString().padStart(2, '0'),
        label: monthLabels[key as keyof typeof monthLabels]
      }
      return obj;
    }));

    const yearsList = [];
    for (let i = 2024; i <= new Date().getFullYear(); i++) {
      yearsList.push({ id: i.toString(), label: i.toString() });
    }
    setYears(yearsList);

    getUserControllerApi().getAllParticipants().then(participants => {
      setParticipants(participants.map(participant => participant && ({
        id: participant.userId ?? "",
        label: participant.email ?? ""
      })) ?? []);
      participants[0] && setParticipantId(participants[0].userId);

      setParticipantDtos(participants);
    });
  }, []);

  useEffect(() => {
    setIncidentsSeries([
      { type: 'bar', dataKey: 'count', color: '#134e4a', label: 'Vorfälle' },
    ]);

    if (participantId) {
      const selectedParticipant = participantDtos.find(participant => participant.userId === participantId);
      setSelectedParticipant(selectedParticipant);
    }

    if (participantId && month && year) {
      getDaySheetControllerApi().getAllDaySheetByParticipantAndMonth({
        userId: participantId,
        month: `${year}-${month}`,
      }).then(daySheets => {
        setDaySheets(daySheets);

        const incidentCountPerDay: { dayLabel: string, count: number }[] = [];
        const dayCountPerSelectedMonth = new Date(parseInt(year), parseInt(month), 0).getDate();
        const data: any[] = [];
        const dataSeriesSet: AllSeriesType[] = [];

        for (let i = 1; i <= dayCountPerSelectedMonth; i++) {
          const daySheet = daySheets.find(daySheet => {
            if (daySheet?.date) {
              const dayOfMonth = new Date(daySheet.date).getDate();
              return dayOfMonth === i;
            }
            return false;
          });

          const monthLabelIndex = Object.keys(monthLabels)[parseInt(month) - 1] as keyof typeof monthLabels;
          const dayLabel = `${i}. ${monthLabels[monthLabelIndex].substring(0, 3)}`;

          incidentCountPerDay.push({
            dayLabel,
            count: daySheet?.incidents?.length ?? 0,
          });

          let workHours = daySheet?.timeSum ?? 0;
          workHours = workHours / (1000 * 60 * 60);
          workHours = Math.round(workHours * 100) / 100;

          const dataItem: any = {
            dayLabel,
            workHours: workHours,
          }
          const moodRatings = daySheet?.moodRatings ?? [];

          moodRatings.forEach(rating => {
            const categoryId = `${rating.ratingRole}_${rating.category?.id}`

            if (rating.rating !== undefined && rating.category?.name && (categorySelection === categorySelections.ALL || (categorySelection === categorySelections.PARTICIPANT && rating.ratingRole === RatingDtoRatingRoleEnum.Participant) || (categorySelection === categorySelections.SOCIAL_WORKER && rating.ratingRole === RatingDtoRatingRoleEnum.SocialWorker))) {
              const min = rating.category?.minimumValue ?? 0;
              const max = rating.category?.maximumValue ?? 100;

              dataItem[categoryId] = (rating.rating - min) / (max - min) * 100;

              if (!dataSeriesSet.find(series => series.id === categoryId)) {
                dataSeriesSet.push({
                  type: 'bar',
                  id: categoryId,
                  dataKey: categoryId,
                  yAxisKey: 'rightAxis',
                  label: rating.category.name,
                });
              }
            }
          });
          data.push(dataItem);
        }

        let indexParticpant = 0;
        let indexSocialWorker = 0;

        dataSeriesSet.forEach((series) => {
          console.log(series)
          if ((series?.id as String)?.startsWith(RatingDtoRatingRoleEnum?.Participant)) {
            series.color = getNextColor("#5eead5", indexParticpant++)
          } else if ((series?.id as String)?.startsWith(RatingDtoRatingRoleEnum?.SocialWorker)) {
            series.color = getNextColor("#ff7f50", indexSocialWorker++)
          }
        });

        dataSeriesSet.push({ type: 'line', dataKey: 'workHours', color: '#000', label: "Arbeitszeit", yAxisKey: 'leftAxis' });

        setIncidentCountPerDay(incidentCountPerDay);
        setDataset(data);
        setDataSeries(dataSeriesSet);
        console.log(dataSeriesSet)
      });
    }
  }, [categorySelection, participantId, month, year]);

  return (
    <>
      <div className="h-full w-full flex flex-col">
        <div className="flex flex-col lg:flex-row justify-between">
          <div className="flex flex-row mb-3 lg:mb-0">
            <Title1>Monatsbericht</Title1>
            <div className="ml-4">
              <ReportGenerator
                month={`${year}-${month}`}
                participant={selectedParticipant}
                daySheets={daySheets}
              />
            </div>
          </div>
          <div className="mt-2 lg:mt-0">
            <Select
              className="w-32 inline-block mr-4 mb-4"
              placeholder="Monat"
              data={months}
              value={month}
              onChange={(e) => setMonth(e.target.value)} />
            <Select
              className="w-24 inline-block mr-4 mb-4"
              placeholder="Jahr"
              data={years}
              value={year}
              onChange={(e) => setYear(e.target.value)} />
            <Select
              className="w-48 inline-block mr-4 mb-4"
              placeholder="Kategorien"
              data={categories}
              value={categorySelection}
              onChange={(e) => setCategorySelection(e.target.value)} />
            <Select
              className="w-40 inline-block mb-4"
              placeholder="Teilnehmer"
              data={participants}
              value={participantId}
              onChange={(e) => setParticipantId(e.target.value)} />
          </div>
        </div>

        <div className="h-full overflow-x-auto flex flex-col space-y-4">
          <div className="min-w-[2200px] bg-white rounded-xl h-36">
            <ResponsiveChartContainer
              series={incidentsSeries as unknown as LineSeriesType[]}
              xAxis={[
                {
                  scaleType: 'band',
                  dataKey: 'dayLabel',
                },
              ]}
              yAxis={[
                { id: 'leftAxis' },
              ]}
              dataset={incidentCountPerDay}
            >
              <ChartsGrid horizontal />
              <BarPlot />
              <LinePlot />
              <ChartsXAxis />
              <ChartsYAxis axisId="leftAxis" label="Vorfälle" />
              <ChartsTooltip trigger="item" faded="global" />
            </ResponsiveChartContainer>
          </div>

          <div className="min-w-[2200px] bg-white rounded-xl grow">
            <ResponsiveChartContainer
              series={dataSeries as unknown as LineSeriesType[]}
              xAxis={[
                {
                  scaleType: 'band',
                  dataKey: 'dayLabel',
                },
              ]}
              yAxis={[
                { id: 'leftAxis' },
                { id: 'rightAxis' }
              ]}
              dataset={dataset}
            >
              <ChartsGrid horizontal />
              <BarPlot />
              <LinePlot />
              <MarkPlot />
              <ChartsXAxis />
              <ChartsYAxis
                axisId="leftAxis"
                position="left"
                label="Arbeitszeit (in h)"
              />
              <ChartsYAxis
                axisId="rightAxis"
                position="right"
                label="Stimmungskategorien (in %)"
              />
              <ChartsTooltip />
            </ResponsiveChartContainer>
          </div>
        </div>
      </div>
    </>
  );
};