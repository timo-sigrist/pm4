import { UserProvider } from '@auth0/nextjs-auth0/client';
import type { Metadata } from "next";
import "./globals.css";
import { Toaster } from "react-hot-toast";

export const metadata: Metadata = {
  title: "Compass",
  description: "",
};

const toastOptions = {
  duration: 4000
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <Toaster position="bottom-center" toastOptions={toastOptions}/>
        <UserProvider>{children}</UserProvider>
      </body>
    </html>
  );
}
