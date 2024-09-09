"use client";

import type React from "react";

export default function IconButton({ onClick, className, type, Icon }: Readonly<{
  onClick?: () => void;
  className?: string;
  type?: "button" | "submit" | "reset";
  Icon?: React.ElementType;
}>) {
  return (
    <button 
      onClick={onClick} 
      type={type ?? "button"}
      className={ `cursor-pointer bg-black rounded-md text-white py-2 pl-[0.6rem] w-10 text-sm drop-shadow flex flex-row hover:bg-slate-800 duration-200 focus:outline-0 ${className}` }>
      {Icon && ( <Icon className="text-white w-5 h-5 mr-2.5" /> )}
    </button>
  );
}