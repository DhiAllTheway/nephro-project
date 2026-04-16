export interface BiologicalResult {
  id?: number;
  parameterName: string;
  value: number;
  unit?: string;
  normalMinValue?: number;
  normalMaxValue?: number;
}
