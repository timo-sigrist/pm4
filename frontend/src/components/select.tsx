import { useEffect, useState } from "react";

export default function Select({ className, placeholder, name, data, required, value, onChange }: Readonly<{
  className?: string;
  placeholder?: string;
  name?: string
  data: Array<{ id?: string, label?: string}>;
  required?: boolean;
  value?: string;
  onChange?: (event: any) => void;
}>) {
  return (
    <>
      <select
        value={value}
        onChange={onChange}
        name={name}
        className={`${className} px-3 py-2 bg-slate-200 text-sm rounded-md focus:outline-2 focus:outline-black duration-200 placeholder:text-slate-400`} >
          <option value="" disabled>{placeholder}</option>
        {data.map((item, index) => {
          return (
            <option key={index} value={item.id}>{item.label}</option>
          )
        })}
      </select>
    </>
  );
}