export function getFormattedDate(date: Date): string {
  return Intl.DateTimeFormat('de-CH',{
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(date);
}

export function getFormattedDateTime(date: Date): string {
  return Intl.DateTimeFormat('de-CH',{
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
}