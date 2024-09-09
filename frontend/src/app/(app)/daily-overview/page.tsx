"use client"

import { getDaySheetControllerApi, getUserControllerApi } from "@/openapi/connector";
import Table from "@/components/table";
import { useEffect, useState } from "react";
import Title1 from "@/components/title1";
import type { DaySheetDto } from "@/openapi/compassClient";
import toastMessages from "@/constants/toastMessages";
import toast from "react-hot-toast";
import Select from "@/components/select";
import { CheckboxChecked24Filled, CheckboxUnchecked24Filled, Note24Regular, Save24Regular, ShiftsProhibited24Regular } from "@fluentui/react-icons";
import Modal from "@/components/modal";
import Button from "@/components/button";
import TextArea from "@/components/textarea";
import { convertMilisecondsToTimeString } from "@/utils/time";
import ConfirmModal from "@/components/confirmmodal";

const allParticipants = "ALL_PARTICIPANTS";

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

enum formFields {
  DAY_NOTES = "dayNotes"
}

function DayNotesModal({ close, onSave, daySheetDto }: Readonly<{
  close: () => void;
  onSave: () => void;
  daySheetDto?: DaySheetDto;
}>) {
  const [notes, setNotes] = useState(daySheetDto?.dayNotes || '');

  const onSubmit = () => {
    const updateAction = () => getDaySheetControllerApi().updateDayNotes({
      updateDaySheetDayNotesDto: {
        id: daySheetDto?.id,
        dayNotes: notes
      }
    }).then(() => {
      close();
      onSave();
    });

    toast.promise(updateAction(), {
      loading: toastMessages.UPDATING,
      success: toastMessages.DAYNOTES_UPDATED,
      error: toastMessages.DAYNOTES_NOT_UPDATED,
    });
  }

  return (
    <Modal
      title="Notizen bearbeiten"
      footerActions={
        <Button Icon={Save24Regular} type="submit">Speichern</Button>
      }
      close={close}
      onSubmit={onSubmit}
    >
      <TextArea
        name={formFields.DAY_NOTES}
        placeholder='Notizen'
        value={notes}
        onChange={(e) => setNotes(e.target.value.slice(0, 255))}
        className='w-full min-h-28' />
      <span className="float-right">{notes.length} / 255</span>
    </Modal>
  );
}

export default function DailyOverviewPage() {
  const [loading, setLoading] = useState(true);
  const [showDayNotesModal, setShowDayNotesModal] = useState(false);
  const [showRevokeConfirmModal, setShowRevokeConfirmModal] = useState(false);

  const [participantSelection, setParticipantSelection] = useState<any>();
  const [month, setMonth] = useState<string>(new Date().toLocaleString('en-US', { month: '2-digit' }).padStart(2, '0'));
  const [year, setYear] = useState<string>(new Date().getFullYear().toString());
  const [selectedDaySheet, setSelectedDaySheet] = useState<DaySheetDto>();
  const [state, setState] = useState<any>({});

  const [daySheets, setDaySheets] = useState<DaySheetDto[]>([]);
  const [daySheetsFiltered, setDaySheetsFiltered] = useState<DaySheetDto[]>([]);
  const [participantSelections, setParticipantSelections] = useState<{ id: string, label: string }[]>([]);
  const [months, setMonths] = useState<{ id: string, label: string }[]>([]);
  const [years, setYears] = useState<{ id: string, label: string }[]>([]);

  const loadDaySheets = () => {
    if (month && year && participantSelection) {
      setLoading(true);
      getDaySheetControllerApi().getAllDaySheetByMonth({
        month: `${year}-${month}`,
      }).then((daySheetsDtos: DaySheetDto[]) => {
        daySheetsDtos.sort((a, b) => {
          const dateA = a?.date ? new Date(a.date).getTime() : 0;
          const dateB = b?.date ? new Date(b.date).getTime() : 0;
          return dateA - dateB;
        });
        setDaySheets(daySheetsDtos);
      }).catch(() => {
        toast.error(toastMessages.DAYSHEETS_NOT_LOADED);
      }).finally(() => {
        setLoading(false);
      });
    }
  }

  const filterDaySheets = () => {
    setDaySheetsFiltered(daySheets.filter(daySheet => {
      let showDaySheet = true;

      if (participantSelection !== allParticipants) {
        daySheet.owner?.userId !== participantSelection && (showDaySheet = false);
      }

      if (state === "CONFIRMED") {
        !daySheet.confirmed && (showDaySheet = false);
      } else if (state === "UNCONFIRMED") {
        daySheet.confirmed && (showDaySheet = false);
      }

      return showDaySheet;
    }));
  }

  const revokeDaySheet = (id: number) => {
    const revokeAction = () => getDaySheetControllerApi().revoke({ id }).then(() => {
      close();
      loadDaySheets();
    });

    toast.promise(revokeAction(), {
      loading: toastMessages.REVOKING,
      success: toastMessages.DAYSHEET_REVOKED,
      error: toastMessages.DAYSHEET_NOT_REVOKED,
    });
  }

  useEffect(() => {
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
      const participantsSelections = participants.map(participant => participant && ({
        id: participant.userId ?? "",
        label: participant.email ?? ""
      })) ?? [];

      participantsSelections.unshift({ id: allParticipants, label: "Alle Teilnehmer" });
      setParticipantSelections(participantsSelections);
      setParticipantSelection(allParticipants);
    });
  }, []);

  useEffect(() => {
    loadDaySheets();
  }, [month, year]);

  useEffect(() => {
    if (loading) {
      loadDaySheets();
    } else {
      filterDaySheets();
    }
  }, [daySheets, participantSelection, state]);

  return (
    <>
      {showDayNotesModal && (
        <DayNotesModal
          close={() => setShowDayNotesModal(false)}
          onSave={loadDaySheets}
          daySheetDto={selectedDaySheet} />
      )}
      {showRevokeConfirmModal && (
        <ConfirmModal
          title="Arbeitszeit Bestätigung"
          question="Möchten Sie die Arbeitszeit Bestätigung rückgängig machen?"
          confirm={() => {
            selectedDaySheet?.id && revokeDaySheet(selectedDaySheet.id);
            setShowRevokeConfirmModal(false);
          }}
          abort={() => setShowRevokeConfirmModal(false)} />
      )}
      <div className="h-full flex flex-col">
        <div className="flex flex-col md:flex-row justify-between">
          <Title1>Tagesübersicht</Title1>
          <div className="mt-2 md:mt-0">
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
              className="w-28 inline-block mr-4 mb-4"
              placeholder="Status"
              data={[
                { id: "ALL", label: "Alle" },
                { id: "CONFIRMED", label: "Bestätigt" },
                { id: "UNCONFIRMED", label: "Nicht bestätigt" }
              ]}
              value={state}
              onChange={(e) => setState(e.target.value)} />
            <Select
              className="w-40 inline-block mb-4"
              placeholder="Teilnehmer"
              data={participantSelections}
              value={participantSelection}
              onChange={(e) => setParticipantSelection(e.target.value)} />
          </div>
        </div>
        <Table
          data={daySheetsFiltered}
          columns={[
            {
              header: "Datum",
              title: "date",
            },
            {
              header: "Arbeitszeit",
              titleFunction: (daySheet: DaySheetDto) => convertMilisecondsToTimeString(daySheet?.timeSum ?? 0)
            },
            {
              header: "Arbeitszeit bestätigt",
              titleFunction: (daySheet: DaySheetDto) => {
                if (daySheet?.confirmed) {
                  return (
                    <div>
                      <CheckboxChecked24Filled className="w-5 h-5 -mt-1" />
                      <span className="ml-2">Ja</span>
                    </div>
                  );
                } else {
                  return (
                    <div>
                      <CheckboxUnchecked24Filled className="w-5 h-5 -mt-1" />
                      <span className="ml-2">Nein</span>
                    </div>
                  );
                }
              }
            },
            {
              header: "Notizen",
              titleFunction: (daySheet: DaySheetDto) => {
                if (daySheet?.dayNotes?.length && daySheet?.dayNotes?.length > 20) {
                  return daySheet?.dayNotes?.slice(0, 20) + "...";
                } else {
                  return daySheet?.dayNotes;
                }
              }
            },
            {
              header: "Teilnehmer",
              titleFunction: (daySheet: DaySheetDto) => daySheet?.owner?.email ?? ""
            }
          ]}
          actions={[
            {
              icon: Note24Regular,
              label: "Notizen",
              onClick: (id) => {
                setSelectedDaySheet(daySheets[id]);
                setShowDayNotesModal(true);
              },
            },
            {
              icon: ShiftsProhibited24Regular,
              hide: (id) => !daySheets[id]?.confirmed ?? false,
              onClick: (id) => {
                setSelectedDaySheet(daySheets[id]);
                setShowRevokeConfirmModal(true);
              },
            }
          ]}
          loading={loading} />
      </div>
    </>
  );
}