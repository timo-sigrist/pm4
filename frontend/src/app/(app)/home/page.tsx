'use client';

import Title1 from "@/components/title1";
import { useUser } from "@auth0/nextjs-auth0/client";
import { Poppins } from "next/font/google";
import { useRouter } from "next/navigation";

const poppins = Poppins({
  subsets: ['latin'],
  weight: ["400", "500", "600"],
})

export default function IncidentsPage() {
  const router = useRouter();
  const { user } = useUser();

  return (  
    <div className={`h-full flex flex-col md:pt-10 lg:px-10 xl:px-20 overflow-y-auto ${poppins.className}`}>
      <div className="flex flex-col lg:flex-row lg:space-x-8 space-y-4 lg:space-y-0">
        <div className="flex-1 lg:py-16">
          {user?.given_name ? 
            <Title1>Willkommen, {String(user.given_name)}! 👋🏼</Title1>
            : <Title1>Willkommen! 👋🏼</Title1>
          }
          <p className="font-light mt-4 mb-4 lg:mb-0">
            Dies ist die Applikation der Stadtmuur für die Erfassung von Arbeitszeiten und der Stimmung.
          </p>
        </div>
        <span 
          className="lg:flex-[2] bg-no-repeat bg-center bg-cover h-[30vh] md:h-[40vh] w-full rounded-xl drop-shadow-xl bg-slate-300" 
          style={{ backgroundImage: "url('https://stadtmuur.ch/wp-content/uploads/2023/11/cropped-header-scaled-3.jpg')" }}
        ></span>
      </div>
      <div className="lg:h-36 md:space-x-8 space-y-4 md:space-y-0 flex flex-col md:flex-row mt-10">
        <div 
          className="rounded-xl border-2 h-full border-slate-300 bg-slate-200 flex-1 py-4 px-6 lg:py-6 lg:px-8 cursor-pointer hover:border-slate-400 hover:bg-slate-300 duration-200 drop-shadow-xl"
          onClick={() => router.push("/working-hours")}
        >
          <b className="text-sm">Arbeitszeit erfassen</b>
          <p className="text-sm mt-1">Wie viel hast du heute gearbeitet? Erfasse deine Zeit hier.</p>
        </div>
        <div 
          className="rounded-xl border-2 h-full border-slate-300 bg-slate-200 flex-1 py-4 px-6 lg:py-6 lg:px-8 cursor-pointer hover:border-slate-400 hover:bg-slate-300 duration-200 drop-shadow-xl"
          onClick={() => router.push("/moods")}
        >
          <b className="text-sm">Stimmung erfassen</b>
          <p className="text-sm mt-1">Wie fühlst du dich heute? Gib es hier an.</p>
        </div>
      </div>
    </div>
  );
}