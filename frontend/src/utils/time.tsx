
export function convertMilisecondsToTimeString(miliseconds: number) {
    const hours = Math.floor(miliseconds / 3600000);
    const minutes = Math.floor((miliseconds % 3600000) / 60000);
    return `${hours}h ${minutes}min`;
}