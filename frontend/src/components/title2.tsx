import { Poppins } from 'next/font/google'

const poppins = Poppins({
  subsets: ['latin'],
  weight: "600",
})

export default function Title2({ children, className }: Readonly<{
  children: React.ReactNode;
  className?: string;
}>) {
  return (
    <h2 className={`text-xl font-bold block ${poppins.className} ${className}`}>
      {children}
    </h2>
  );
}