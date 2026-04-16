export interface CalendarEvent {
  reportId: number;
  patientId: number;
  type: 'REPORT' | 'NORMAL' | 'FOLLOW_UP' | 'URGENT';
  date: string;
  label: string;
  severity: number;
}