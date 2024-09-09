import { Poppins } from 'next/font/google'

const poppins = Poppins({
  subsets: ['latin'],
  weight: "600",
})

export default function Title1({ children, className }: Readonly<{
  children: React.ReactNode;
  className?: string;
}>) {
  return (
    <h1 className={`text-xl leading-9 font-bold block ${poppins.className} ${className}`}>
      {children}
    </h1>
  );
}