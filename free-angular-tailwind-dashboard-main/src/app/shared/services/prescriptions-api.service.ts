import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Prescription } from '../models/prescription.model';
import { PrescribedMedication } from '../models/prescribed-medication.model';
import { ValidationResponse } from '../models/validation-response.model';

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  number: number;
  size: number;
  totalElements: number;
}

export interface AiExplanationResponse {
  prescriptionId: number;
  model: string;
  explanation: string;
  warning: string;
}

@Injectable({ providedIn: 'root' })
export class PrescriptionsApiService {
  private baseUrl = 'http://localhost:8082/prescriptions/prescriptions';

  constructor(private http: HttpClient) {}

  getPrescriptions(patientId?: number, date?: string, page = 0, size = 10): Observable<PageResponse<Prescription>> {
    const params: any = { page, size };
    if (patientId != null) params.patientId = patientId;
    if (date) params.date = date;

    return this.http.get<PageResponse<Prescription>>(this.baseUrl, { params });
  }

  getPrescriptionById(id: number): Observable<Prescription> {
    return this.http.get<Prescription>(`${this.baseUrl}/${id}`);
  }

  createPrescription(p: Prescription): Observable<Prescription> {
    return this.http.post<Prescription>(this.baseUrl, p);
  }

  deletePrescription(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getMedications(prescriptionId: number): Observable<PrescribedMedication[]> {
    return this.http.get<PrescribedMedication[]>(`${this.baseUrl}/${prescriptionId}/medications`);
  }

  addMedication(prescriptionId: number, medication: PrescribedMedication): Observable<PrescribedMedication> {
    return this.http.post<PrescribedMedication>(`${this.baseUrl}/${prescriptionId}/medications`, medication);
  }

  updateMedication(prescriptionId: number, medicationId: number, medication: PrescribedMedication): Observable<PrescribedMedication> {
    return this.http.put<PrescribedMedication>(`${this.baseUrl}/${prescriptionId}/medications/${medicationId}`, medication);
  }

  deleteMedication(prescriptionId: number, medicationId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${prescriptionId}/medications/${medicationId}`);
  }

  signPrescription(prescriptionId: number, payload: { signatureData: string; signedBy?: string }): Observable<Prescription> {
    return this.http.post<Prescription>(`${this.baseUrl}/${prescriptionId}/sign`, payload);
  }

  validatePrescription(prescriptionId: number): Observable<ValidationResponse> {
    return this.http.get<ValidationResponse>(`${this.baseUrl}/${prescriptionId}/validation`);
  }

  explainPrescription(prescriptionId: number): Observable<AiExplanationResponse> {
    return this.http.get<AiExplanationResponse>(`${this.baseUrl}/${prescriptionId}/ai-explanation`);
  }
}