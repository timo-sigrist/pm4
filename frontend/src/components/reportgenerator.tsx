"use client";

import React from "react";
import Docxtemplater from "docxtemplater";
import PizZip from "pizzip";
import { saveAs } from "file-saver";
import Button from "./button";
import type { DaySheetDto, UserDto } from "@/openapi/compassClient";
import { convertMilisecondsToTimeString } from "@/utils/time";
import { ArrowDownload24Regular } from "@fluentui/react-icons";

let PizZipUtils: { getBinaryContent?: any; default?: any; } | null = null;
if (typeof window !== "undefined") {
    import("pizzip/utils/index.js").then(function (r) {
        PizZipUtils = r;
    });
}

function loadFile(url: string, callback: (error: any, content: any) => void) {
    if (PizZipUtils) {
        PizZipUtils.getBinaryContent(url, callback);
    }
}

function generateDocument(data: any, fileName: string) {
    loadFile(
        "/report-template.docx",
        function (error, content) {
            if (error) {
                throw error;
            }
            const zip = new PizZip(content);
            const doc = new Docxtemplater(zip, {
                linebreaks: true,
                paragraphLoop: true,
            });

            doc.render(data);
            const blob = doc.getZip().generate({
                type: "blob",
                mimeType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            });

            saveAs(blob, fileName);
        }
    );
}

export default function ReportGenerator({ className, month, participant, daySheets }: Readonly<{
    className?: string;
    month: string;
    participant: UserDto | undefined;
    daySheets: DaySheetDto[];
}>) {
    const generateReport = () => {
        daySheets = daySheets.sort((a, b) => {
            const dateA = a?.date ? new Date(a.date).getDate() : 0;
            const dateB = b?.date ? new Date(b.date).getDate() : 0;
            return dateA - dateB;
        });

        const days = daySheets.map((daySheet) => {
            const day = daySheet?.date ? Intl.DateTimeFormat("de-DE", {
                weekday: "long",
                day: "numeric",
                month: "long",
                year: "numeric",
            }).format(new Date(daySheet.date)) : "";

            const incidents = daySheet?.incidents ? daySheet.incidents.map((incident) => {
                return {
                    title: incident.title,
                    description: incident.description,
                };
            }) : [];

            return {
                day: day,
                confirmed: !!daySheet.confirmed,
                time: convertMilisecondsToTimeString(daySheet.timeSum ?? 0),
                notes: daySheet?.dayNotes ?? "-",
                incidents: incidents,
            };
        });

        const generationDate = Intl.DateTimeFormat("de-DE", {
            day: "numeric",
            month: "long",
            year: "numeric",
        }).format(new Date());

        const owner = {
            name: `${participant?.givenName} ${participant?.familyName}`,
            email: participant?.email,
        }

        const data = {
            month: month,
            days: days,
            owner: owner,
            generationDate: generationDate,
        }

        const fileName = `${month} ${owner.name} Rapport.docx`;
        generateDocument(data, fileName);
    };

    return (
        <Button
            Icon={ArrowDownload24Regular}
            className={className}
            onClick={generateReport}
        >Rapport</Button>
    );
}