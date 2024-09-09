import { useEffect } from "react";
import { Checkmark24Regular, Dismiss24Regular } from '@fluentui/react-icons';
import Button from "./button";
import Title3 from "./title3";

export default function ConfirmModal({ title, question, confirm, abort }: Readonly<{
    title: string;
    question: string;
    confirm: () => void;
    abort: () => void;
}>) {
    useEffect(() => {
        const handleEsc = (event: any) => {
            if (event.keyCode === 27) abort();
        };

        window.addEventListener('keydown', handleEsc);
    }, []);

    return (
        <div className="absolute top-0 right-0 bottom-0 left-0 bg-slate-900/40 backdrop-blur-sm z-30">
            <div className="mx-5 md:mx-0">
                <div className="bg-white p-6 rounded-xl mt-48 mx-auto w-full md:w-96">
                    <div className="flex flex-row justify-between">
                        <Title3 className="leading-9">{title}</Title3>
                        <button
                            type="button"
                            className="px-2 py-1.5 hover:bg-slate-100 duration-200 rounded-md focus:outline-2 focus:outline-black"
                            onClick={abort}
                        >
                            <Dismiss24Regular className="color-black w-5 h-5 -mt-1" />
                        </button>
                    </div>
                    <div className="mt-4 text-sm">
                        <p>{question}</p>
                    </div>
                    <div className="mt-7 flex space-x-5 max-w-80 md:max-w-full mx-auto">
                        <Button
                            Icon={Checkmark24Regular}
                            className="w-full text-right"
                            onClick={confirm}
                        >Bestätigen</Button>
                        <button
                            type="button"
                            className="text-sm px-4 py-2 bg-slate-100 hover:bg-slate-200 duration-200 rounded-md focus:outline-2 focus:outline-black w-full"
                            onClick={abort}
                        >
                            Abbrechen
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}