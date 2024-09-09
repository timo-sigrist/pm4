"use client";

import { useState, useEffect } from "react";
import Modal from "@/components/modal";
import Button from "@/components/button";
import Input from "@/components/input";
import Select from "@/components/select";
import Table from "@/components/table";
import { AppsAddIn24Regular, Save24Regular } from "@fluentui/react-icons";
import { getUserControllerApi, getCategoryControllerApi } from "@/openapi/connector";
import { CategoryDto, UserDto } from "@/openapi/compassClient";
import Title1 from "@/components/title1";
import { toast } from "react-hot-toast";
import toastMessages from "@/constants/toastMessages";
import ConfirmModal from "@/components/confirmmodal";

function CategoryCreateModal({ close, onSave, category }: Readonly<{
  close: () => void;
  onSave: () => void;
  category: CategoryDto | null;
}>) {
  const [showCreateConfirmModal, setShowCreateConfirmModal] = useState<boolean>(false);
  const [name, setName] = useState<string>(category?.name || "");
  const [min, setMin] = useState<number>(category?.minimumValue || 0);
  const [max, setMax] = useState<number>(category?.maximumValue || 10);
  const [assignment, setAssignment] = useState<string>("global");
  const [selectedParticipants, setSelectedParticipants] = useState<UserDto[]>([]);
  const [participants, setParticipants] = useState<UserDto[]>([]);

  const loadUsers = () => {
    getUserControllerApi().getAllParticipants().then((users) => {
      setParticipants(users);
    });
  };

  const createCategory = () => {
    const newCategory: CategoryDto = {
      name: name,
      minimumValue: min,
      maximumValue: max,
      categoryOwners: assignment === "custom" ? selectedParticipants : [],
    };

    const createAction = () => getCategoryControllerApi().createCategory({
      categoryDto: newCategory
    }).then(() => {
      onSave();
      close();
    });

    toast.promise(createAction(), {
      loading: toastMessages.CREATING,
      success: toastMessages.CATEGORY_CREATED,
      error: toastMessages.CATEGORY_NOT_CREATED,
    });
  };

  useEffect(loadUsers, []);

  return (
    <>
      <Modal
        title="Kategorie erstellen"
        footerActions={
          <Button Icon={Save24Regular} type="submit">
            Speichern
          </Button>
        }
        close={close}
        onSubmit={() => setShowCreateConfirmModal(true)}
      >
        <div>
          <Input
            type="text"
            placeholder="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="mb-4"
            required={true}
          />
          <div>
            <div>
              <span className="bg-black text-white px-3 py-2.5 rounded-l-md text-sm">von</span>
              <Input
                type="number"
                placeholder="Min"
                value={min.toString()}
                onChange={(e) => setMin(Number(e.target.value))}
                className="mb-4 mr-4 w-24 rounded-none rounded-r-md"
                required={true}
              />
            </div>
            <div>
              <span className="bg-black text-white px-3 py-2.5 rounded-l-md text-sm">bis</span>
              <Input
                type="number"
                placeholder="Max"
                value={max.toString()}
                onChange={(e) => setMax(Number(e.target.value))}
                className="mb-4 mr-4 w-24 rounded-none rounded-r-md"
                required={true}
              />
            </div>
          </div>
          <Select
            value={assignment}
            onChange={(e) => setAssignment(e.target.value)}
            className="mb-4"
            data={[
              { id: "global", label: "Alle Teilnehmer" },
              { id: "custom", label: "Teilnehmer auswählen" },
            ]}
          />
        </div>
        <div>
          {assignment === "custom" && (
            <div>
              {participants.map((participant) => (
                <div
                  key={participant.userId}
                  className="flex items-center mb-1 bg-slate-100 hover:bg-slate-200 rounded-md"
                >
                  <input
                    type="checkbox"
                    checked={selectedParticipants.some(
                      (p) => p.userId === participant.userId
                    )}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setSelectedParticipants([
                          ...selectedParticipants,
                          participant,
                        ]);
                      } else {
                        setSelectedParticipants(
                          selectedParticipants.filter(
                            (p) => p.userId !== participant.userId
                          )
                        );
                      }
                    }}
                    className="w-4 h-4 ml-4"
                  />
                  <span className="text-sm px-3 py-2">
                    {participant.givenName} {participant.familyName} ({participant.email})
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </Modal>
      {showCreateConfirmModal && (
        <ConfirmModal
          title="Kategorie erstellen"
          question="Möchtest du die Kategorie wirklich erstellen? Die Kategorie kann im Anschluss nicht mehr bearbeitet werden."
          confirm={() => {
            createCategory();
            setShowCreateConfirmModal(false);
          }}
          abort={() => setShowCreateConfirmModal(false)} />
      )}
    </>
  );
};

export default function CategoryPage() {
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [selectedCategory, setSelectedCategory] = useState<CategoryDto | null>(null);
  const [categories, setCategories] = useState<CategoryDto[]>([]);

  const loadCategories = () => {
    setLoading(true);
    getCategoryControllerApi().getAllCategories().then((categories) => {
      categories.sort((a, b) => (a?.name ?? '').localeCompare(b?.name ?? ''));
      setCategories(categories);
    }).catch(() => {
      toast.error(toastMessages.CATEGORIES_NOT_LOADED);
    }).finally(() => {
      setLoading(false);
    });
  }

  useEffect(loadCategories, []);

  return (
    <>
      {showModal && (
        <CategoryCreateModal
          close={() => setShowModal(false)}
          onSave={loadCategories}
          category={selectedCategory}
        />
      )}
      <div className="h-full flex flex-col">
        <div className="flex flex-col sm:flex-row justify-between mb-4">
          <Title1>Kategorien</Title1>
          <div className="mt-2 sm:mt-0">
            <Button Icon={AppsAddIn24Regular} onClick={() => {
              setShowModal(true);
              setSelectedCategory(null);
            }}>Erstellen</Button>
          </div>
        </div>
        <Table
          data={categories}
          columns={[
            {
              header: "Kategorie",
              title: "name"
            },
            {
              header: "Von",
              title: "minimumValue"
            },
            {
              header: "Bis",
              title: "maximumValue"
            },
            {
              header: "Zuweisung",
              titleFunction: (category: CategoryDto) => {
                if (category.categoryOwners?.length) {
                  return category.categoryOwners.map((owner) => owner.email).join(", ");
                }
                return "Alle Teilnehmer";
              }
            },
          ]}
          actions={[]}
          loading={loading}
        />
      </div>
    </>
  );
};
