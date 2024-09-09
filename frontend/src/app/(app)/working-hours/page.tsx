'use client';

import Title1 from "@/components/title1";
import Table from "@/components/table";
import Input from "@/components/input";
import { ArrowLeft24Regular, ArrowRight24Regular, Checkmark24Filled, Delete24Regular, Edit24Regular, Save24Regular, ShiftsAdd24Regular } from "@fluentui/react-icons";
import { FormEvent, useEffect, useState } from "react";
import { getDaySheetControllerApi, getTimestampControllerApi } from "@/openapi/connector";
import Button from "@/components/button";
import Modal from "@/components/modal";
import toast from "react-hot-toast";
import toastMessages from "@/constants/toastMessages";
import { CreateDaySheetRequest, CreateTimestampRequest, DaySheetDto, TimestampDto } from "@/openapi/compassClient";
import IconButton from "@/components/iconbutton";
import { convertMilisecondsToTimeString } from "@/utils/time";

function TimestampUpdateModal({ close, onSave, timestamp }: Readonly<{
  close: () => void;
  onSave: () => void;
  timestamp: TimestampDto | undefined;
}>) {
  const [updatedTimestamp, setTimestamp] = useState<{ startTime: string; endTime: string; }>({ startTime: '', endTime: '' });

  const onSubmit = (formData: FormData) => {
    const editedTimestamp: TimestampDto = {
      id: timestamp?.id || 0,
      daySheetId: timestamp?.daySheetId || 0,
      startTime: formData.get('startTime') as string,
      endTime: formData.get('endTime') as string
    };

    if (editedTimestamp.endTime && editedTimestamp.startTime && editedTimestamp.endTime <= editedTimestamp.startTime) {
      toast.error(toastMessages.STARTTIME_AFTER_ENDTIME);
      return;
    }

    const updateTimestampAction = () => getTimestampControllerApi().putTimestamp({ timestampDto: editedTimestamp }).then(() => {
      close();
      onSave();
    })

    toast.promise(updateTimestampAction(), {
      loading: toastMessages.UPDATING,
      success: toastMessages.TIMESTAMP_UPDATED,
      error: toastMessages.TIMESTAMP_NOT_UPDATED
    });
  }

  const handleTimeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setTimestamp(prevState => ({ ...prevState, [name]: value }));
  };

  useEffect(() => { setTimestamp({ startTime: timestamp?.startTime || "00:00", endTime: timestamp?.endTime || "00:00" }) }, []);

  return (
    <Modal
      title="Zeiteintrag bearbeiten"
      footerActions={
        <Button Icon={Save24Regular} type="submit">Speichern</Button>
      }
      close={close}
      onSubmit={onSubmit}
    >
      <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="startTime" required={true} value={updatedTimestamp.startTime} onChange={handleTimeChange} />
      <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="endTime" required={true} value={updatedTimestamp.endTime} onChange={handleTimeChange} />
    </Modal>
  );
}


export default function WorkingHoursPage() {
  const [loading, setLoading] = useState(true);
  const [daySheet, setDaySheet] = useState<DaySheetDto>({ id: -1, date: new Date(), dayNotes: '', timestamps: [], timeSum: 0, confirmed: false });
  const [selectedTimestamp, setSelectedTimestamp] = useState<TimestampDto>();
  const [selectedDate, setSelectedDate] = useState<string>(new Date().toISOString().slice(0, 10));
  const [showUpdateModal, setShowUpdateModal] = useState(false);

  const handlePrevDate = () => {
    let selectedDateObj = new Date(selectedDate);
    const newDate = new Date(selectedDate);

    newDate.setDate(selectedDateObj.getDate() - 1);
    setSelectedDate(newDate.toISOString().slice(0, 10));
  };

  const handleNextDate = () => {
    let selectedDateObj = new Date(selectedDate);
    const newDate = new Date(selectedDate);

    newDate.setDate(selectedDateObj.getDate() + 1);
    setSelectedDate(newDate.toISOString().slice(0, 10));
  };

  const loadDaySheetByDate = (date: string) => {
    setLoading(true);
    getDaySheetControllerApi().getDaySheetDate({ date: date }).then((daySheetDto: DaySheetDto) => {
      const loadedDaySheet: DaySheetDto = {
        id: daySheetDto.id || 0,
        date: new Date(daySheetDto.date || ''),
        dayNotes: String(daySheetDto.dayNotes || ''),
        timestamps: [],
        timeSum: daySheetDto.timeSum || 0,
        confirmed: daySheetDto.confirmed || false
      };

      daySheetDto.timestamps?.sort((a: TimestampDto, b: TimestampDto) => {
        const startTimeAHour = a.startTime?.split(':').map(Number)[0] ?? 0;
        const startTimeBHour = b.startTime?.split(':').map(Number)[0] ?? 0;

        return startTimeAHour - startTimeBHour;
      }).forEach((timestamp: TimestampDto) => {
        if (loadedDaySheet?.timestamps && timestamp.startTime && timestamp.endTime) {
          loadedDaySheet.timestamps.push({
            id: timestamp.id || 0,
            daySheetId: timestamp.daySheetId || 0,
            startTime: timestamp.startTime.substring(0, 5),
            endTime: timestamp.endTime.substring(0, 5)
          });
        }
      });
      setDaySheet(loadedDaySheet);
    }).catch(() => {
      const emptyDaySheet = {
        id: 0,
        date: new Date(),
        dayNotes: '',
        timestamps: [],
        timeSum: 0,
        confirmed: false
      };
      setDaySheet(emptyDaySheet);
    }).finally(() => {
      setLoading(false);
    });
  }

  const onCreateTimestampSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    event.stopPropagation();

    const formData = new FormData(event.target as HTMLFormElement);
    const startTime = formData.get('startTime') as string;
    const endTime = formData.get('endTime') as string;

    getDaySheetControllerApi().getDaySheetDate({ date: selectedDate }).then((daySheetDto: DaySheetDto) => {
      daySheetDto?.id && createTimestamp(daySheetDto.id, startTime, endTime);
    }).catch(() => {
      const creatDaySheetDto: CreateDaySheetRequest = {
        daySheetDto: {
          date: new Date(selectedDate),
          dayNotes: '',
          timestamps: [],
          confirmed: false
        }
      };

      getDaySheetControllerApi().createDaySheet(creatDaySheetDto).then((createdDaySheet: DaySheetDto) => {
        createdDaySheet?.id && createTimestamp(createdDaySheet.id, startTime, endTime);
      })
    });
  };

  const createTimestamp = (daySheetId: number, startTime: string, endTime: string) => {
    if (endTime <= startTime) {
      toast.error(toastMessages.STARTTIME_AFTER_ENDTIME);
      return;
    }

    const createTimestampRequest: CreateTimestampRequest = {
      timestampDto: {
        daySheetId: daySheetId,
        startTime: startTime,
        endTime: endTime
      }
    };

    const createAction = () => getTimestampControllerApi().createTimestamp(createTimestampRequest).then(() => {
      loadDaySheetByDate(selectedDate)
    });

    toast.promise(createAction(), {
      loading: toastMessages.CREATING,
      success: toastMessages.TIMESTAMP_CREATED,
      error: toastMessages.TIMESTAMP_NOT_CREATED
    });
  };

  const deleteTimestamp = (id: number) => {
    const deleteAction = () => getTimestampControllerApi().deleteTimestamp({ id }).then(() => {
      loadDaySheetByDate(selectedDate);
    });

    toast.promise(deleteAction(), {
      loading: toastMessages.DELETING,
      success: toastMessages.TIMESTAMP_DELETED,
      error: toastMessages.TIMESTAMP_NOT_DELETED
    });
  }

  useEffect(() => loadDaySheetByDate(selectedDate), [selectedDate]);

  return (
    <>
      {showUpdateModal && (
        <TimestampUpdateModal
          close={() => {
            setShowUpdateModal(false)
            loadDaySheetByDate(selectedDate)
          }}
          onSave={() => loadDaySheetByDate(selectedDate)}
          timestamp={selectedTimestamp} />
      )}
      <div className="h-full flex flex-col">
        <div className="flex flex-col md:flex-row justify-between mb-4">
          <div>
            <Title1 className="inline-block">Arbeitszeit erfassen</Title1>
            {daySheet.id !== -1 && daySheet.confirmed && (
              <div className="text-sm inline-block ml-4 cursor-pointer text-green-500" >
                <Checkmark24Filled className="w-4 h-4 inline-block mr-2 cursor-pointer" />
                <span className="inline-block hover:underline">Daten wurden bestätigt</span>
              </div>
            )}
          </div>
          <div className="mt-2 md:mt-0 flex flex-row">
            <IconButton
              className="rounded-none rounded-l-md"
              Icon={ArrowLeft24Regular}
              onClick={handlePrevDate} />
            <Input
              className="rounded-none"
              type="date"
              name="date"
              value={selectedDate}
              onChange={event => setSelectedDate(event.target.value)} />
            <IconButton
              className="rounded-none rounded-r-md"
              Icon={ArrowRight24Regular}
              onClick={handleNextDate} />
          </div>
        </div>
        <Table
          data={daySheet?.timestamps ?? []}
          columns={[
            {
              header: "Start-Uhrzeit",
              title: "startTime"
            },
            {
              header: "End-Uhrzeit",
              title: "endTime"
            },
            {
              header: "Dauer",
              titleFunction: (timestamp: TimestampDto) => {
                const startDate = new Date(`01/01/2000 ${timestamp.startTime}`);
                const endDate = new Date(`01/01/2000 ${timestamp.endTime}`);
                const timestampDuration = endDate.getTime() - startDate.getTime();
                return convertMilisecondsToTimeString(timestampDuration);
              }
            }
          ]}
          actions={[
            {
              icon: Delete24Regular,
              hide: () => daySheet?.confirmed ?? false,
              onClick: (id) => {
                const timestampId = daySheet?.timestamps?.[id]?.id;
                timestampId && deleteTimestamp(timestampId);
              }
            },
            {
              icon: Edit24Regular,
              hide: () => daySheet?.confirmed ?? false,
              onClick: (id) => {
                if (!daySheet.confirmed) {
                  if (daySheet?.timestamps) {
                    let timestamp = daySheet.timestamps[id];
                    if (timestamp) timestamp.daySheetId = daySheet.id;
                    setSelectedTimestamp(timestamp);
                    setShowUpdateModal(true);
                  }
                } else {
                  toast.error(toastMessages.DAYSHEET_ALREADY_CONFIRMED);
                }
              }
            }
          ]}
          loading={loading}
          customBottom={
            <tr className="bg-white border-t-4 border-slate-100">
              <td colSpan={2} className="py-4 px-6 text-left text-sm font-bold">
                Gesamt
              </td>
              <td colSpan={2} className="py-4 px-6 text-left text-sm font-bold">
                {convertMilisecondsToTimeString(daySheet?.timeSum ?? 0)}
              </td>
            </tr>
          }
        />
        {daySheet.id !== -1 && !daySheet.confirmed && (
          <form className="mt-4 flex flex-col sm:flex-row sm:space-x-4" onSubmit={onCreateTimestampSubmit}>
            <Input type="time" className="mb-4 sm:w-48" name="startTime" />
            <Input type="time" className="mb-4 sm:w-48" name="endTime" />
            <div>
              <Button Icon={ShiftsAdd24Regular} type="submit" className="mb-4 bg-black text-white rounded-md">Erfassen</Button>
            </div>
          </form>
        )}
      </div>
    </>
  );
};