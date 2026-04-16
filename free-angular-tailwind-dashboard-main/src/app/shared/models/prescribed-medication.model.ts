export interface PrescribedMedication {
  id?: number;
  medicationName: string;
  dosage: string;
  frequency: string;
  durationDays?: number;
}