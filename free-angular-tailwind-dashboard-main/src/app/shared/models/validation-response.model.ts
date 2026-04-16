export type ValidationLevel = 'OK' | 'WARN' | 'ERROR';

export interface ValidationResponse {
  level: ValidationLevel;
  message: string;

  patientId: number;

  labReportId?: number;
  reportDate?: string;

  abnormalCount?: number;
  maxSeverity?: number;
}