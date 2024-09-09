'use client';
import React, { FormEvent, useEffect, useState } from 'react';
import Table from "@/components/table";
import { ArrowLeft24Regular, Checkmark24Regular, Delete24Regular, Edit24Regular, Save24Regular, ShiftsAdd24Regular } from "@fluentui/react-icons";
import Title1 from "@/components/title1";
import Button from "@/components/button";
import { toast } from "react-hot-toast";
import Modal from "@/components/modal";
import Input from "@/components/input";
import { CreateTimestampRequest, DaySheetDto, TimestampDto, type ConfirmRequest } from "@/openapi/compassClient";
import { getDaySheetControllerApi, getTimestampControllerApi } from "@/openapi/connector";
import toastMessages from "@/constants/toastMessages";
import { convertMilisecondsToTimeString } from '@/utils/time';
import { getFormattedDate } from '@/utils/date';
import { useRouter } from 'next/navigation';
import ConfirmModal from '@/components/confirmmodal';

function TimeStampUpdateModal({ close, onSave, timestamp }: Readonly<{
  close: () => void;
  onSave: () => void;
  timestamp: TimestampDto | undefined;
}>) {
  const [updatedTimestamp, setTimestamp] = useState<{ startTime: string; endTime: string; }>({ startTime: timestamp?.startTime || "00:00", endTime: timestamp?.endTime || "00:00" });

  const onSubmit = (formData: FormData) => {
    const editedTimestamp: TimestampDto = {
      id: timestamp?.id || 0,
      daySheetId: timestamp?.daySheetId || 0,
      startTime: formData.get('startTime') as string,
      endTime: formData.get('endTime') as string
    };

    const updateAction = () => getTimestampControllerApi().putTimestamp({ timestampDto: editedTimestamp }).then(() => {
      close();
      setTimeout(() => onSave(), 1000);
    });

    toast.promise(updateAction(), {
      loading: toastMessages.UPDATING,
      success: toastMessages.TIMESTAMP_UPDATED,
      error: toastMessages.TIMESTAMP_NOT_UPDATED
    });
  }

  const handleTimeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setTimestamp(prevState => ({ ...prevState, [name]: value }));
  };

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

export default function WorkingHoursCheckByIdPage({ params }: { params: { id: number } }) {
  const [showConfirmConfirmModal, setShowConfirmConfirmModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [daySheet, setDaySheet] = useState<DaySheetDto>();
  const [selectedTimestamp, setSelectedTimestamp] = useState<TimestampDto>();
  const [timestamps, setTimestamps] = useState<TimestampDto[]>([]);
  const [showUpdateModal, setShowUpdateModal] = useState(false);

  const router = useRouter();

  const loadTimestamps = () => {
    setLoading(true);
    Promise.all([
      getTimestampControllerApi().getAllTimestampByDaySheetId({ id: params.id }).then(timestamps => {
        close();
        timestamps.sort((a, b) => {
          const startTimeAHour = a.startTime?.split(':').map(Number)[0] ?? 0;
          const startTimeBHour = b.startTime?.split(':').map(Number)[0] ?? 0;

          return startTimeAHour - startTimeBHour;
        });
        timestamps.forEach(timestamp => {
          timestamp.startTime = timestamp.startTime?.slice(0, 5);
          timestamp.endTime = timestamp.endTime?.slice(0, 5);
        });
        setTimestamps(timestamps);
      }),
      getDaySheetControllerApi().getDaySheetById({ id: params.id }).then(daySheet => {
        setDaySheet(daySheet);
      })
    ]).catch(() => {
      toast.error(toastMessages.TIMESTAMPS_NOT_LOADED);
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

    if (endTime <= startTime) {
      toast.error(toastMessages.STARTTIME_AFTER_ENDTIME);
      return;
    }

    const createTimestampRequest: CreateTimestampRequest = {
      timestampDto: {
        daySheetId: daySheet?.id,
        startTime: startTime,
        endTime: endTime
      }
    };

    const createAction = () => getTimestampControllerApi().createTimestamp(createTimestampRequest).then(() => {
      loadTimestamps();
    });

    toast.promise(createAction(), {
      loading: toastMessages.CREATING,
      success: toastMessages.TIMESTAMP_CREATED,
      error: toastMessages.TIMESTAMP_NOT_CREATED
    });
  };

  const deleteTimestamp = (id: number) => {
    const deleteAction = () => getTimestampControllerApi().deleteTimestamp({ id }).then(() => {
      loadTimestamps();
    }).catch((error) => {
      console.log("Error while deleting timestamp")
      console.error(error);
    })

    toast.promise(deleteAction(), {
      loading: toastMessages.DELETING,
      success: toastMessages.TIMESTAMP_DELETED,
      error: toastMessages.TIMESTAMP_NOT_DELETED
    });
  }

  const confirmDaySheet = async (id: number) => {
    const updateDayRequest: ConfirmRequest = {
      id: id
    };

    const confirmAction = () => getDaySheetControllerApi().confirm(updateDayRequest).then(() => {
      close();
      router.push("/working-hours-check");
    })

    toast.promise(confirmAction(), {
      loading: toastMessages.CONFIRMING,
      success: toastMessages.DAYSHEET_CONFIRMED,
      error: toastMessages.DAYSHEET_NOT_CONFIRMED,
    });
  };

  useEffect(() => {
    loadTimestamps();
  }, []);

  return (
    <>
      {showUpdateModal && (
        <TimeStampUpdateModal
          close={() => setShowUpdateModal(false)}
          onSave={loadTimestamps}
          timestamp={selectedTimestamp} />
      )}
      {showConfirmConfirmModal && (
        <ConfirmModal
          title="Arbeitszeit bestätigen"
          question="Möchten Sie die Arbeitszeit bestätigen? Dies kann rückgängig gemacht werden."
          confirm={() => {
            daySheet?.id && confirmDaySheet(daySheet.id);
            setShowConfirmConfirmModal(false);
          }}
          abort={() => setShowConfirmConfirmModal(false)} />
      )}
      <div className="flex flex-col md:flex-row justify-between">
        <div>
          <Title1 className="mb-4 inline-block">Kontrolle Arbeitszeit</Title1>
          <div
            className="text-sm inline-block ml-4 cursor-pointer"
            onClick={() => router.push("/working-hours-check")}>
            <ArrowLeft24Regular className="w-4 h-4 inline-block mr-2 cursor-pointer" onClick={() => window.history.back()} />
            <span className="inline-block hover:underline">Zurück zur Übersicht</span>
          </div>
        </div>
        <div className="mb-4 md:mt-0">
          {daySheet ? (
            <>
              <span className="leading-9 text-sm">
                {daySheet?.owner?.givenName} {daySheet?.owner?.familyName}
              </span>
              <span className="mx-2">|</span>
              <span className="font-bold leading-9 text-sm">
                {daySheet?.date && getFormattedDate(daySheet?.date)}
              </span>
            </>
          ) : (
            <>
              <div className="w-28 h-4 my-3 rounded-md bg-slate-300 animate-pulse mr-4 inline-block"></div>
              <div className="w-20 h-4 my-3 rounded-md bg-slate-300 animate-pulse inline-block"></div>
            </>
          )}
        </div>
      </div>
      <Table
        data={timestamps}
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
            onClick: (id) => {
              const timestampId = timestamps?.[id]?.id;
              timestampId && deleteTimestamp(timestampId);
            }
          },
          {
            icon: Edit24Regular,
            onClick: (id) => {
              setSelectedTimestamp(timestamps[id]);
              setShowUpdateModal(true);
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
        } />
      <form className="mt-4 flex flex-col sm:flex-row sm:space-x-4" onSubmit={onCreateTimestampSubmit}>
        <Input type="time" className="mb-4 sm:w-48" name="startTime" />
        <Input type="time" className="mb-4 sm:w-48" name="endTime" />
        <div>
          <Button Icon={ShiftsAdd24Regular} type="submit" className="mb-4 bg-black text-white rounded-md">Erfassen</Button>
        </div>
        <div className="grow"></div>
        <div>
          <Button
            Icon={Checkmark24Regular}
            type="button"
            className="mb-4 bg-black text-white rounded-md"
            onClick={() => setShowConfirmConfirmModal(true)}>Bestätigen</Button>
        </div>
      </form>
    </>
  );
};
