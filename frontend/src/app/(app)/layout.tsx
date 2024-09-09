"use client";

import { useUser } from "@auth0/nextjs-auth0/client";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";

import HomeIcon from "@fluentui/svg-icons/icons/home_24_regular.svg";
import HomeIconFilled from "@fluentui/svg-icons/icons/home_24_filled.svg";

// participant
import WorkingHoursIcon from "@fluentui/svg-icons/icons/shifts_24_regular.svg";
import WorkingHoursIconFilled from "@fluentui/svg-icons/icons/shifts_24_filled.svg";
import MoodIcon from "@fluentui/svg-icons/icons/person_feedback_24_regular.svg";
import MoodIconFilled from "@fluentui/svg-icons/icons/person_feedback_24_filled.svg";
import IncidentIcon from "@fluentui/svg-icons/icons/alert_24_regular.svg";
import IncidentIconFilled from "@fluentui/svg-icons/icons/alert_24_filled.svg";

// social worker
import WorkingHoursCheckIcon from "@fluentui/svg-icons/icons/shifts_checkmark_24_regular.svg";
import WorkingHoursCheckIconFilled from "@fluentui/svg-icons/icons/shifts_checkmark_24_filled.svg";
import DailyOverviewIcon from "@fluentui/svg-icons/icons/calendar_day_24_regular.svg";
import DailyOverviewIconFilled from "@fluentui/svg-icons/icons/calendar_day_24_filled.svg";
import MonthlyOverviewIcon from "@fluentui/svg-icons/icons/calendar_month_24_regular.svg";
import MonthlyOverviewIconFilled from "@fluentui/svg-icons/icons/calendar_month_24_filled.svg";
import CategoryIcon from "@fluentui/svg-icons/icons/apps_24_regular.svg";
import CategoryIconFilled from "@fluentui/svg-icons/icons/apps_24_filled.svg";

// admin
import UserIcon from "@fluentui/svg-icons/icons/people_24_regular.svg";
import UserIconFilled from "@fluentui/svg-icons/icons/people_24_filled.svg";

import ExpandMenuIcon from "@fluentui/svg-icons/icons/chevron_right_24_regular.svg";
import CollapseMenuIcon from "@fluentui/svg-icons/icons/chevron_left_24_regular.svg";
import MenuIcon from "@fluentui/svg-icons/icons/list_24_regular.svg";
import MenuCloseIcon from "@fluentui/svg-icons/icons/dismiss_24_regular.svg";
import Roles from "@/constants/roles";
import { getSystemControllerApi, getUserControllerApi } from "@/openapi/connector";
import type { SystemStatusDto, UserDto } from "@/openapi/compassClient";
import { Checkmark24Filled, Copy24Regular, Dismiss24Filled } from "@fluentui/react-icons";
import { setTimeout } from "timers";

const SubTitle: React.FC<{ collapsed: boolean, label: string, withLine?: boolean }> = ({ collapsed, label, withLine }) => {
  return (
    collapsed ? (
      withLine && (
        <div className="px-2">
          <div className="h-[1px] mt-4 mb-1 bg-slate-400 w-full"></div>
        </div>
      )
    ) : (
      <p className="mt-5 text-sm text-slate-400 font-semibold">{label}</p>
    )
  );
}

const MenuItem: React.FC<{ collapsed: boolean, icon: { src: string }; iconActive?: { src: string }; label: string, route?: string, onClick?: any, className?: any }> = ({ collapsed, icon, iconActive, label, route, onClick, className }) => {
  const router = useRouter();
  const pathname = usePathname();

  const onClickHandler = () => {
    if (route) router.push(route);
    if (onClick) onClick();
  }

  const isActive = pathname === route || pathname.includes(`${route}/`);
  const iconSrc = isActive && iconActive ? iconActive.src : icon.src;

  return (
    <div
      className={`${className} ${collapsed ? "mt-3 px-1.5 py-1.5 " : "mt-1 flex flex-row px-3 py-2.5"} rounded-lg cursor-pointer hover:bg-slate-100 ${isActive ? 'bg-gradient-to-r from-slate-100 to-slate-200' : ''}`}
      onClick={onClickHandler}
    >
      <img src={iconSrc} className="w-5 h-5 mr-2.5" />
      {!collapsed ? (<p className="text-sm">{label}</p>) : null}
    </div>
  );
}

const Profile: React.FC<{ user: any, userRole: string | undefined, systemStatus: SystemStatusDto | undefined, isLoading: boolean }> = ({ user, userRole, systemStatus, isLoading }) => {
  const [showMenu, setShowMenu] = useState(false);
  const [commitCopyActive, setCommitCopyActive] = useState(false);

  const handleClickOutsideMenu = (event: MouseEvent) => {
    const menu = document.getElementById("profile-menu");
    if (menu && !menu.contains(event.target as Node)) {
      setShowMenu(false);
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutsideMenu);
    return () => {
      document.removeEventListener("mousedown", handleClickOutsideMenu);
    };
  }, []);

  return (
    <>
      <button
        className="absolute top-5 right-5 rounded-full flex duration-150 hover:bg-slate-200 cursor-pointer"
        onClick={() => setShowMenu(!showMenu)}
      >
        {isLoading ? (
          <>
            <span className="leading-10 ml-4 mr-3 mt-3 text-sm h-4 w-14 rounded-md bg-slate-300 animate-pulse"></span>
            <div className="relative">
              <div className="h-10 w-10 border-2 border-slate-400 bg-slate-400 rounded-full animate-pulse"></div>
            </div>
          </>
        ) : (
          <>
            <span className="leading-10 ml-4 mr-3 text-sm">{user?.given_name ? user?.given_name : user?.nickname}</span>
            <div className="relative">
              <img src={user?.picture} alt="" className="h-10 w-10 border-2 border-slate-400 bg-slate-400 rounded-full" />
            </div>
          </>
        )}
      </button>
      {showMenu && (
        <div id="profile-menu" className="left-5 sm:left-auto absolute top-20 right-5 px-8 py-7 bg-white rounded-3xl flex flex-col drop-shadow-lg">
          {user?.given_name && user?.family_name ? (<span className="font-bold text-sm">{user?.given_name} {user?.family_name}</span>) : null}
          <span className="mb-4 text-sm">{user?.email}</span>
          {userRole === Roles.ADMIN || !userRole ? (
            <div className="py-4 border-t-[1px] border-slate-200 text-sm">
              <div>
                <span className="font-bold inline-block">Systemstatus</span>
                {systemStatus?.commitId && (
                  <span
                    className="text-slate-500 cursor-pointer ml-2 text-[0.8rem] hover:underline relative"
                    onClick={() => navigator.clipboard.writeText(systemStatus?.commitId ?? "")}>
                    <div
                      className="inline-block"
                      onClick={() => {
                        setCommitCopyActive(true)
                        setTimeout(() => setCommitCopyActive(false), 1000)
                      }}>
                      <span>#{systemStatus?.commitId?.substring(0, 7)}</span>
                      {commitCopyActive ? (
                        <Checkmark24Filled className="-mt-1 w-4 h-4 ml-1 text-slate-500" />
                      ) : (
                        <Copy24Regular className="-mt-1 w-4 h-4 ml-1 text-slate-500" />
                      )}
                    </div>
                  </span>
                )}
                {systemStatus?.backendIsReachable ? (
                  <div className="mt-1 flex flex-row">
                    <Checkmark24Filled className="w-5 h-5 text-green-500" />
                    <span className="block ml-2">Backend erreichbar</span>
                  </div>
                ) : (
                  <div className="mt-1 flex flex-row">
                    <Dismiss24Filled className="w-5 h-5 text-red-500" />
                    <span className="block ml-2">Backend nicht erreichbar</span>
                  </div>
                )}
                {systemStatus?.databaseIsReachable ? (
                  <div className="flex flex-row">
                    <Checkmark24Filled className="w-5 h-5 text-green-500" />
                    <span className="block ml-2">Datenbank erreichbar</span>
                  </div>
                ) : (
                  <div className="flex flex-row">
                    <Dismiss24Filled className="w-5 h-5 text-red-500" />
                    <span className="block ml-2">Datenbank nicht erreichbar</span>
                  </div>
                )}
                {systemStatus?.auth0IsReachable ? (
                  <div className="flex flex-row">
                    <Checkmark24Filled className="w-5 h-5 text-green-500" />
                    <span className="block ml-2">Auth0 erreichbar</span>
                  </div>
                ) : (
                  <div className="flex flex-row">
                    <Dismiss24Filled className="w-5 h-5 text-red-500" />
                    <span className="block ml-2">Auth0 nicht erreichbar</span>
                  </div>
                )}
              </div>
            </div>
          ) : null}
          <a href="/api/auth/logout" className="pt-4 border-t-[1px] border-slate-200 text-sm hover:text-slate-600 duration-150">Logout</a>
        </div>
      )}
    </>
  );
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const { isLoading, user } = useUser();
  const [backendUser, setBackendUser] = useState<UserDto>();
  const [systemStatus, setSystemStatus] = useState<SystemStatusDto>();
  const [menuOpen, setMenuOpen] = useState(false);

  const toggleMenu = () => setMenuOpen(!menuOpen);
  const handleMobileClick = () => window.innerWidth < 1024 && toggleMenu();

  useEffect(() => {
    getSystemControllerApi().getStatus().then(systemStatus => {
      setSystemStatus(systemStatus);
    });
  }, []);

  useEffect(() => {
    user?.sub && getUserControllerApi().getUserById({ id: user.sub }).then(userDto => {
      setBackendUser(userDto)
    })
  }, [user])

  return (
    <>
      <div className="lg:flex lg:flex-row h-screen w-screen absolute">
        <div className={`${menuOpen ? "w-full lg:w-64 lg:min-w-64" : "hidden lg:block lg:w-16 lg:min-w-16"} absolute lg:relative border-r-[1px] border-slate-300 z-20 h-full bg-white overflow-y-auto`}>
          <div className={`${menuOpen ? "p-5 w-full" : "p-4"}  flex flex-col h-full`}>
            <div className="flex">
              {menuOpen ? (
                <h1 className="text-lg font-bold grow leading-9">Compass 🧭</h1>
              ) : (
                <h1 className="text-lg px-1.5 py-1.5">🧭</h1>
              )}
              <button className="p-2 bg-white hover:bg-slate-100 duration-150 rounded-md lg:hidden" onClick={() => setMenuOpen(!menuOpen)}>
                <img src={MenuCloseIcon.src} className="w-5 h-5" />
              </button>
            </div>
            <SubTitle collapsed={!menuOpen} label="Allgemein" />
            <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={HomeIcon} iconActive={HomeIconFilled} label="Home" route="/home" />

            {backendUser && backendUser.role === Roles.PARTICIPANT && (
              <>
                <SubTitle collapsed={!menuOpen} label="Teilnehmer" withLine={true} />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={WorkingHoursIcon} iconActive={WorkingHoursIconFilled} label="Arbeitszeit" route="/working-hours" />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={MoodIcon} iconActive={MoodIconFilled} label="Stimmung" route="/moods" />
              </>
            )}

            {backendUser && (backendUser.role === Roles.SOCIAL_WORKER || backendUser.role === Roles.ADMIN) && (
              <>
                <SubTitle collapsed={!menuOpen} label="Sozialarbeiter" withLine={true} />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={MoodIcon} iconActive={MoodIconFilled} label="Stimmung" route="/moods" />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={IncidentIcon} iconActive={IncidentIconFilled} label="Vorfälle" route="/incidents" />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={WorkingHoursCheckIcon} iconActive={WorkingHoursCheckIconFilled} label="Kontrolle Arbeitszeit" route="/working-hours-check" />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={DailyOverviewIcon} iconActive={DailyOverviewIconFilled} label="Tagesübersicht" route="/daily-overview" />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={MonthlyOverviewIcon} iconActive={MonthlyOverviewIconFilled} label="Monatsübersicht" route="/monthly-overview" />
              </>
            )}

            {backendUser && backendUser.role === Roles.ADMIN && (
              <>
                <SubTitle collapsed={!menuOpen} label="Admin" withLine={true} />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={UserIcon} iconActive={UserIconFilled} label="Benutzer" route="/users" />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={CategoryIcon} iconActive={CategoryIconFilled} label="Kategorien" route="/categories" />
              </>
            )}

            <div className="grow"></div>
            {menuOpen ? (
              <MenuItem className="hidden lg:flex" collapsed={!menuOpen} icon={CollapseMenuIcon} label="Zuklappen" onClick={toggleMenu} />
            ) : (
              <MenuItem className="hidden lg:flex mb-2" collapsed={true} icon={ExpandMenuIcon} label="Expandieren" onClick={toggleMenu} />
            )}
          </div>
        </div>
        <div className="grow pt-20 z-10 lg:pt-0 bg-slate-100 h-full">
          <div className="w-full h-full lg:container lg:mx-auto px-5 lg:pt-24 pb-16">
            <div className="h-full w-full">
              {children}
              <Profile
                user={user}
                userRole={backendUser?.role}
                systemStatus={systemStatus}
                isLoading={isLoading} />
            </div>
          </div>
        </div>
        <button className="absolute left-5 top-5 block lg:hidden p-2 hover:bg-slate-200 duration-150 rounded-md" onClick={toggleMenu}>
          <img src={MenuIcon.src} className="w-5 h-5" />
        </button>
      </div>
    </>
  );
}
