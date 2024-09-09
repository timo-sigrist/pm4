"use client"

import { getUserControllerApi } from "@/openapi/connector";
import Button from "@/components/button";
import Input from "@/components/input";
import Modal from "@/components/modal";
import Select from "@/components/select";
import Table from "@/components/table";
import Title1 from "@/components/title1";
import Roles from "@/constants/roles";
import { PersonAdd24Regular, Delete24Regular, Edit24Regular, Save24Regular, ArrowHookUpLeft24Regular } from "@fluentui/react-icons";
import { useEffect, useState } from "react";
import { toast } from 'react-hot-toast';
import toastMessages from "@/constants/toastMessages";
import RoleTitles from "@/constants/roleTitles";
import type { AuthZeroUserDtoRoleEnum, CreateAuthZeroUserDtoRoleEnum, CreateUserRequest, UpdateUserRequest, UserDto } from "@/openapi/compassClient";
import ConfirmModal from "@/components/confirmmodal";

const auth0Timeout = 3000;

const roles = [
  {
    id: Roles.PARTICIPANT,
    label: RoleTitles[Roles.PARTICIPANT]
  },
  {
    id: Roles.SOCIAL_WORKER,
    label: RoleTitles[Roles.SOCIAL_WORKER]
  },
  {
    id: Roles.ADMIN,
    label: RoleTitles[Roles.ADMIN]
  }
]

enum formFields {
  GIVEN_NAME = "givenName",
  FAMILY_NAME = "familyName",
  ROLE = "role",
  EMAIL = "email",
  PASSWORD = "password"
}

function UserCreateModal({ close, onSave }: Readonly<{
  close: () => void;
  onSave: () => void;
}>) {
  const onSubmit = (formData: FormData) => {
    const createUserDto: CreateUserRequest = {
      createAuthZeroUserDto: {
        email: formData.get(formFields.EMAIL) as string,
        givenName: formData.get(formFields.GIVEN_NAME) as string,
        familyName: formData.get(formFields.FAMILY_NAME) as string,
        role: formData.get(formFields.ROLE) as CreateAuthZeroUserDtoRoleEnum,
        password: formData.get(formFields.PASSWORD) as string
      }
    };

    const createAction = () => getUserControllerApi().createUser(createUserDto).then(() => {
      close();
      return new Promise<void>((resolve) => setTimeout(() => {
        onSave();
        resolve();
      }, auth0Timeout));
    });

    toast.promise(createAction(), {
      loading: toastMessages.CREATING,
      success: toastMessages.USER_CREATED,
      error: toastMessages.USER_NOT_CREATED,
    });
  }

  return (
    <Modal
      title="Benutzer erstellen"
      footerActions={
        <Button Icon={Save24Regular} type="submit">Speichern</Button>
      }
      close={close}
      onSubmit={onSubmit}
    >
      <Input type="text" placeholder="Vorname" className="mb-4 mr-4 w-48 inline-block" name={formFields.GIVEN_NAME} required={true} />
      <Input type="text" placeholder="Nachname" className="mb-4 mr-4 w-48 inline-block" name={formFields.FAMILY_NAME} required={true} />
      <Select
        className="mb-4 mr-4 w-32 block"
        placeholder="Rolle wählen"
        name={formFields.ROLE}
        data={roles}
        required={true} />
      <Input type="email" placeholder="Email" className="mb-4 mr-4 w-64 block" name={formFields.EMAIL} required={true} />
      <Input type="password" placeholder="Initiales Passwort" className="mb-4 mr-4 w-48 block" name={formFields.PASSWORD} required={true} />
    </Modal>
  );
}

function UserUpdateModal({ close, onSave, user }: Readonly<{
  close: () => void;
  onSave: () => void;
  user: UserDto | undefined;
}>) {
  const [givenName, setGivenName] = useState(user?.givenName);
  const [familyName, setFamilyName] = useState(user?.familyName);

  const onSubmit = (formData: FormData) => {
    const updateUserRequest: UpdateUserRequest = {
      id: user?.userId as string,
      authZeroUserDto: {
        email: formData.get(formFields.EMAIL) as string,
        givenName: formData.get(formFields.GIVEN_NAME) as string,
        familyName: formData.get(formFields.FAMILY_NAME) as string,
        role: formData.get(formFields.ROLE) as AuthZeroUserDtoRoleEnum
      }
    };

    const updateAction = () => getUserControllerApi().updateUser(updateUserRequest).then(() => {
      close();
      return new Promise<void>((resolve) => setTimeout(() => {
        onSave();
        resolve();
      }, auth0Timeout));
    });

    toast.promise(updateAction(), {
      loading: toastMessages.UPDATING,
      success: toastMessages.USER_UPDATED,
      error: toastMessages.USER_NOT_UPDATED,
    });
  }

  return (
    <Modal
      title="Benutzer bearbeiten"
      footerActions={
        <Button Icon={Save24Regular} type="submit">Speichern</Button>
      }
      close={close}
      onSubmit={onSubmit}
    >
      <Input type="text" placeholder="Vorname" className="mb-4 mr-4 w-48 inline-block" name={formFields.GIVEN_NAME} required={true} value={givenName} onChange={(e) => setGivenName(e.target.value)} />
      <Input type="text" placeholder="Nachname" className="mb-4 mr-4 w-48 inline-block" name={formFields.FAMILY_NAME} required={true} value={familyName} onChange={(e) => setFamilyName(e.target.value)} />
      <Select
        className="mb-4 mr-4 w-32 block"
        placeholder="Rolle wählen"
        name={formFields.ROLE}
        data={roles}
        required={true}
        value={user?.role} />
      <Input type="email" placeholder="Email" className="mb-4 mr-4 w-64 block" name={formFields.EMAIL} disabled={true} value={user?.email} />
    </Modal>
  );
}

export default function UsersPage() {
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [showDeleteConfirmModal, setShowDeleteConfirmModal] = useState(false);
  const [users, setUsers] = useState<UserDto[]>([]);
  const [selectedUser, setSelectedUser] = useState<UserDto>();

  const loadUsers = () => {
    setLoading(true);
    getUserControllerApi().getAllUsers().then(userDtos => {
      const users = userDtos.map(user => {
        user.role = user.role ?? Roles.PARTICIPANT;
        const roleTitle = RoleTitles[user.role as Roles];
        return { ...user, roleTitle }
      })
      users.sort((a, b) => (a?.givenName || '').localeCompare(b?.givenName || ''));
      setUsers(users);
    }).catch(() => {
      toast.error(toastMessages.USERS_NOT_LOADED);
    }).finally(() => {
      setLoading(false);
    });
  }

  const deleteUser = (id: string) => {
    const deleteAction = () => getUserControllerApi().deleteUser({ id }).then(() => {
      return new Promise<void>((resolve) => setTimeout(() => {
        loadUsers();
        resolve();
      }, auth0Timeout));
    })

    toast.promise(deleteAction(), {
      loading: toastMessages.DELETING,
      success: toastMessages.USER_DELETED,
      error: toastMessages.USER_NOT_DELETED,
    });
  }

  const restoreUser = (id: string) => {
    const restoreAction = () => getUserControllerApi().restoreUser({ id }).then(() => {
      return new Promise<void>((resolve) => setTimeout(() => {
        loadUsers();
        resolve();
      }, auth0Timeout));
    })

    toast.promise(restoreAction(), {
      loading: toastMessages.RESTORING,
      success: toastMessages.USER_RESTORED,
      error: toastMessages.USER_NOT_RESTORED,
    });
  }

  useEffect(() => loadUsers(), []);

  return (
    <>
      {showCreateModal && (
        <UserCreateModal
          close={() => setShowCreateModal(false)}
          onSave={loadUsers} />
      )}
      {showUpdateModal && (
        <UserUpdateModal
          close={() => setShowUpdateModal(false)}
          onSave={loadUsers}
          user={selectedUser} />
      )}
      {showDeleteConfirmModal && (
        <ConfirmModal
          title="Benutzer löschen"
          question="Möchten Sie diesen Benutzer wirklich löschen? Der Benutzer bleibt im System erhalten."
          confirm={() => {
            selectedUser?.userId && deleteUser(selectedUser.userId);
            setShowDeleteConfirmModal(false);
          }}
          abort={() => setShowDeleteConfirmModal(false)} />
      )}
      <div className="h-full flex flex-col">
        <div className="flex flex-col sm:flex-row justify-between mb-4">
          <Title1>Benutzerverwaltung</Title1>
          <div className="mt-2 sm:mt-0">
            <Button Icon={PersonAdd24Regular} onClick={() => setShowCreateModal(true)}>Erstellen</Button>
          </div>
        </div>
        <Table
          data={users}
          columns={[
            {
              header: "Vorname",
              title: "givenName"
            },
            {
              header: "Nachname",
              title: "familyName"
            },
            {
              header: "Email",
              title: "email"
            },
            {
              header: "Rolle",
              title: "roleTitle"
            }
          ]}
          actions={[
            {
              icon: ArrowHookUpLeft24Regular,
              label: "Restore",
              onClick: (id) => {
                const user = users[id];
                user?.userId && restoreUser(user.userId)
              },
              hide: (id) => {
                const user = users[id] as UserDto;
                return !user.deleted ?? true;
              }
            },
            {
              icon: Delete24Regular,
              label: "Löschen",
              onClick: (id) => {
                setSelectedUser(users[id]);
                setShowDeleteConfirmModal(true);
              },
              hide: (id) => {
                const user = users[id] as UserDto;
                return user.deleted ?? false;
              }
            },
            {
              icon: Edit24Regular,
              onClick: (id) => {
                setSelectedUser(users[id]);
                setShowUpdateModal(true);
              }
            }
          ]}
          loading={loading} />
      </div>
    </>
  );
}