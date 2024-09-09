import type { ChangeEvent } from "react";

export default function TextArea({ className, placeholder, name, required, value, onChange }: Readonly<{
  className?: string;
  placeholder?: string;
  name?: string;
  required?: boolean;
  value?: string;
  onChange?: (event: ChangeEvent<HTMLTextAreaElement>) => void;
}>) {
  return (
    <textarea
      name={name}
      placeholder={placeholder} 
      className={`${className} px-3 py-2 bg-slate-200 text-sm rounded-md focus:outline-2 focus:outline-black duration-200 placeholder:text-slate-400`}
      required={required}
      value={value}
      onChange={onChange} />
  );
}