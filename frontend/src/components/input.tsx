import type { ChangeEvent } from "react";

export default function Input({ className, type, placeholder, name, value, disabled, required, onChange }: Readonly<{
  className?: string;
  type?: string;
  placeholder?: string;
  name?: string;
  value?: string;
  disabled?: boolean;
  required?: boolean;
  onChange?: (event: ChangeEvent<HTMLInputElement>) => void;
}>) {
  return (
    <input
      type={type}
      placeholder={placeholder}
      className={`${className} px-3 py-2 bg-slate-200 text-sm rounded-md focus:outline-2 focus:outline-black duration-200 placeholder:text-slate-400`}
      name={name}
      value={value}
      disabled={disabled}
      required={required}
      onChange={onChange} />
  );
}