import { Poppins } from 'next/font/google'

const poppins = Poppins({
  subsets: ['latin'],
  weight: "600",
})

export default function Title3({ children, className }: Readonly<{
  children: React.ReactNode;
  className?: string;
}>) {
  return (
    <h2 className={`text-lg font-bold block ${poppins.className} ${className}`}>
      {children}
    </h2>
  );
}