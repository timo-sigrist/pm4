"use client";

import type React from "react";

export default function Button({ children, onClick, className, type, Icon }: Readonly<{
  children?: React.ReactNode;
  onClick?: () => void;
  className?: string;
  type?: "button" | "submit" | "reset";
  Icon?: React.ElementType;
}>) {
  return (
    <button
      onClick={onClick}
      type={type ?? "button"}
      className={`text-center cursor-pointer bg-black rounded-md text-white px-4 py-2 text-sm drop-shadow flex flex-row hover:bg-slate-800 duration-200 focus:outline-0 ${className}`}>
      {Icon && (<Icon className="text-white w-5 h-5 mr-2.5" />)}
      {children}
    </button>
  );
}