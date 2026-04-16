import { BiologicalResultDTO } from './biological-result-dto.model';

export interface BiologicalReportDTO {
  id?: number;
  patientId: number;
  reportDate: string;
  analysisType?: string;
  laboratoryName?: string;
  comment?: string;

  results: BiologicalResultDTO[];

  abnormalCount: number;
  reportStatus: 'NORMAL' | 'ABNORMAL';
}