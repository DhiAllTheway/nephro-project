export type BiologicalStatus = 'NORMAL' | 'LOW' | 'HIGH';

export interface BiologicalResultDTO {
  id?: number;
  parameterName: string;
  value: number;
  unit?: string;
  normalMinValue?: number;
  normalMaxValue?: number;

  status: BiologicalStatus;
  severity: number;
  isAbnormal: boolean;
}