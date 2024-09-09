"use client";

import { useState, useEffect } from "react";
import Modal from "@/components/modal";
import Button from "@/components/button";
import Input from "@/components/input";
import Select from "@/components/select";
import Table from "@/components/table";
import { ArrowLeft24Regular, ArrowRight24Regular, PersonFeedback24Regular, Save24Regular } from "@fluentui/react-icons";
import Slider from "@/components/slider";
import {
  getUserControllerApi,
  getCategoryControllerApi,
  getRatingControllerApi,
  getDaySheetControllerApi,
} from "@/openapi/connector";
import { useUser } from "@auth0/nextjs-auth0/client";
import toast from "react-hot-toast";
import {
  CategoryDto,
  UserDtoRoleEnum,
  type DaySheetDto,
  type RatingDto,
} from "@/openapi/compassClient";
import toastMessages from "@/constants/toastMessages";
import Title1 from "@/components/title1";
import IconButton from "@/components/iconbutton";
import ConfirmModal from "@/components/confirmmodal";

const MoodModal = ({
  close,
  onSave,
  categories,
  daySheetId,
  participantId,
  selectedDate
}: {
  close: () => void;
  onSave: () => void;
  categories: any[];
  daySheetId: number | undefined;
  participantId: string | undefined;
  selectedDate: string;
}) => {
  const [showCreateConfirmModal, setShowCreateConfirmModal] = useState(false);
  const [moodValues, setMoodValues] = useState<{ [key: string]: number }>({});

  const handleMoodChange = (categoryId: any, value: number) => {
    setMoodValues((prevValues) => ({
      ...prevValues,
      [String(categoryId)]: value,
    }));
  };

  const saveRatings = () => {
    const ratingsToSave = Object.keys(moodValues).map((categoryId) => ({
      categoryId: Number(categoryId),
      rating: moodValues[categoryId],
    }));

    const saveAction = () => Promise.all([]).then(() => {
      if (!daySheetId) {
        return getDaySheetControllerApi().createDaySheet({
          daySheetDto: {
            date: new Date(selectedDate),
            owner: {
              userId: participantId || "",
            }
          },
        }).then((daySheet: DaySheetDto) => {
          daySheetId = daySheet.id;
          console.log(daySheetId);
        });
      }
      return;
    }).then(() => getRatingControllerApi().createRatingsByDaySheetId({
      daySheetId: daySheetId ?? 0,
      createRatingDto: ratingsToSave,
    }).then(() => {
      onSave();
      close();
    }));

    toast.promise(saveAction(), {
      loading: toastMessages.CREATING,
      success: toastMessages.RATING_CREATED,
      error: toastMessages.RATING_NOT_CREATED,
    });
  };

  useEffect(() => {
    const initialMoodValues = categories.reduce((acc, category) => {
      acc[category.id] = category.minimumValue;
      return acc;
    }, {});

    setMoodValues(initialMoodValues);
  }, []);

  return (
    <>
      <Modal
        title="Bewertung abgeben"
        footerActions={
          <>
            <Button Icon={Save24Regular} type="submit">
              Speichern
            </Button>
          </>
        }
        close={close}
        onSubmit={() => setShowCreateConfirmModal(true)}
      >
        <div className="flex flex-col">
          {categories.map((category) => (
            <div key={category.id} className="mb-4">
              <label className="mb-4 font-bold" >{category.name}</label>
              <Slider
                min={category.minimumValue}
                max={category.maximumValue}
                onChange={(value) => handleMoodChange(category.id, value)}
                name={""}
              />
            </div>
          ))}
        </div>
      </Modal>
      {showCreateConfirmModal && (
        <ConfirmModal
          title="Bewertung abgeben"
          question="Möchten Sie die Bewertung abgeben? Diese kann im Anschluss nicht mehr angepasst werden."
          confirm={() => {
            saveRatings();
            setShowCreateConfirmModal(false);
          }}
          abort={() => setShowCreateConfirmModal(false)} />
      )}
    </>
  );
};

const MoodTrackingPage = () => {
  const [loading, setLoading] = useState(true);

  const { user } = useUser();
  const [role, setRole] = useState<UserDtoRoleEnum | undefined>();
  const [showModal, setShowModal] = useState(false);
  const [ratings, setRatings] = useState<RatingDto[]>([]);
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().slice(0, 10));
  const [participants, setParticipants] = useState<{ id: string, label: string }[]>([]);
  const [selectedParticipant, setSelectedParticipant] = useState<string | undefined>();
  const [daySheetId, setDaySheetId] = useState<number | undefined>(undefined);

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

  const loadUserRole = () => {
    getUserControllerApi().getUserById({
      id: user?.sub ?? "",
    }).then((userDto) => {
      setRole(userDto.role);
      if (userDto.role === UserDtoRoleEnum.SocialWorker || userDto.role === UserDtoRoleEnum.Admin) {
        getUserControllerApi().getAllParticipants().then(participantDtos => {
          const participants = participantDtos.map((participantDto) => ({
            id: participantDto.userId ?? "",
            label: participantDto.email ?? "",
          }));

          setParticipants(participants);
          const firstParticipant = participants[0];
          if (firstParticipant?.id) {
            setSelectedParticipant(firstParticipant.id);
          }
        });
      } else {
        setSelectedParticipant(user?.sub ?? "");
      }
    });
  };

  const loadRatings = () => {
    setLoading(true);
    Promise.all([]).then(() => {
      if (role === UserDtoRoleEnum.Participant) {
        return getDaySheetControllerApi().getDaySheetDate({
          date: selectedDate,
        });
      } else {
        return getDaySheetControllerApi().getDaySheetByParticipantAndDate({
          userId: selectedParticipant || "",
          date: selectedDate,
        });
      }
    }).then((daySheet: DaySheetDto) => {
      daySheet?.moodRatings?.sort((a, b) => {
        const nameA = a.category?.name ?? "";
        const nameB = b.category?.name ?? "";
        const typeA = a.ratingRole ?? "";
        const typeB = b.ratingRole ?? "";

        return nameA.localeCompare(nameB) || typeA.localeCompare(typeB);
      });
      setRatings(daySheet?.moodRatings || []);
      setDaySheetId(daySheet?.id);
    }).catch(() => {
      setRatings([]);
      setDaySheetId(undefined);
    }).finally(() => {
      setLoading(false);
    })
  };

  const loadCategories = (participantId: string) => {
    getCategoryControllerApi().getCategoryListByUserId({
      userId: participantId,
    }).then(setCategories).catch(() => {
      toast.error(toastMessages.CATEGORIES_NOT_LOADED);
    });
  };

  useEffect(() => {
    if (user?.sub) {
      loadUserRole();
    }
  }, [user]);

  useEffect(() => {
    if (selectedParticipant && selectedDate && role) {
      loadRatings();
      loadCategories(selectedParticipant);
    }
  }, [selectedDate, selectedParticipant, role]);

  return (
    <>
      {showModal && (
        <MoodModal
          close={() => setShowModal(false)}
          onSave={loadRatings}
          categories={categories}
          daySheetId={daySheetId}
          participantId={selectedParticipant}
          selectedDate={selectedDate}
        />
      )}
      <div className="h-full flex flex-col">
        <div className="flex flex-col md:flex-row justify-between mb-4">
          <Title1>Stimmung</Title1>
          <div className="mt-2 md:mt-0 flex flex-col md:flex-row space-y-4 md:space-y-0 md:space-x-4">
            {role === UserDtoRoleEnum.SocialWorker || role === UserDtoRoleEnum.Admin ? (
              <Select
                className="w-48 inline-block"
                name="participant"
                required
                data={participants}
                value={selectedParticipant}
                onChange={(event) => setSelectedParticipant(event.target.value)}
              />
            ) : null}
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
        </div>
        <Table
          data={ratings}
          columns={[
            {
              header: "Kategorie",
              titleFunction: (rating: RatingDto) => {
                return rating.category?.name?.trim() ?? "";
              }
            },
            {
              header: "Rolle",
              titleFunction: (rating: RatingDto) => {
                return rating.ratingRole === "PARTICIPANT"
                  ? "Teilnehmer"
                  : "Sozialarbeiter";
              }
            },
            {
              header: "Bewertung",
              titleFunction: (rating: RatingDto) => {
                const ratingValue = rating.rating ?? 0;
                const minimumValue = rating.category?.minimumValue ?? 0;
                const maximumValue = rating.category?.maximumValue ?? 0;
                const ratingWidth = 100 / (maximumValue - minimumValue) * (ratingValue - minimumValue);

                return <div className="h-5 w-full bg-slate-300 rounded-full min-w-32 relative">
                  <div className="h-full bg-black rounded-full absolute" style={{ width: `${ratingWidth}%` }}></div>
                  <span className="font-bold text-white text-sm ml-3 w-full absolute">{rating.rating} / {rating.category?.maximumValue}</span>
                </div>;
              }
            },
          ]}
          actions={[]}
          loading={loading}
        />
        {!loading && (
          <div>
            <Button
              className="mt-4"
              Icon={PersonFeedback24Regular}
              onClick={() => {
                setShowModal(true);
              }}
            >
              Bewertung erfassen
            </Button>
          </div>
        )}
      </div>
    </>
  );
};

export default MoodTrackingPage;
