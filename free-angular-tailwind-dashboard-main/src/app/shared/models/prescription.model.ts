export interface Prescription {
  id?: number;
  patientId: number;
  prescriptionDate: string; // YYYY-MM-DD
  doctorName: string;
  notes?: string;

  // Signature business logic
  status?: 'DRAFT' | 'SIGNED';
  signatureData?: string; // data:image/png;base64,...
  signedAt?: string;      // ISO date-time from backend
  signedBy?: string;
}