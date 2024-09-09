'use client';

import Button from "@/components/button";
import Loading from "@/components/loading";
import { useUser } from "@auth0/nextjs-auth0/client";
import { useRouter } from "next/navigation";

export default function LandingPage() {
  const router = useRouter();
  const { user, isLoading } = useUser();

  const login = () => router.push('/api/auth/login?returnTo=/home');

  if (user) router.push('/home');

  return (
    isLoading || user ? (
      <div className="fixed top-0 right-0 bottom-0 left-0 bg-white">
        <Loading />
      </div>
    ) :
    <div className="w-full h-screen bg-slate-100 backdrop-blur-lg bg-no-repeat bg-center bg-cover flex items-center justify-center">
        <div className="flex-col">
          <h1 className="text-4xl font-bold text-slate-900">Compass 🧭</h1>
          <Button className="mt-4" onClick={login}>Login</Button>
        </div>
    </div>
  );
}