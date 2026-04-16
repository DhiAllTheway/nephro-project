export interface LabReport {
  id?: number;
  patientId: number;
  reportDate: string; // format: "YYYY-MM-DD"
  analysisType?: string;
  laboratoryName?: string;
  comment?: string;
}
