import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { LabReport } from '../models/lab-report.model';
import { BiologicalResult } from '../models/biological-result.model';
import { BiologicalReportDTO } from '../models/biological-report-dto.model';
import { LabReportStatistics } from '../models/lab-report-statistics.model';
import { CalendarEvent } from '../models/calendar-event.model';

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  number: number;
  size: number;
  totalElements: number;
}

@Injectable({ providedIn: 'root' })
export class LabResultsApiService {
  private baseUrl = 'http://localhost:8082/lab-results/lab-reports';

  constructor(private http: HttpClient) {}

  // -------- Reports --------

  getReports(patientId?: number, date?: string, page = 0, size = 10): Observable<PageResponse<LabReport>> {
    const params: any = { page, size };
    if (patientId != null) params.patientId = patientId;
    if (date) params.date = date;
    return this.http.get<PageResponse<LabReport>>(this.baseUrl, { params });
  }

  getReportById(id: number): Observable<LabReport> {
    return this.http.get<LabReport>(`${this.baseUrl}/${id}`);
  }

  getReportDtoById(id: number): Observable<BiologicalReportDTO> {
    return this.http.get<BiologicalReportDTO>(`${this.baseUrl}/${id}/dto`);
  }

  getLatestReportDtoByPatient(patientId: number): Observable<BiologicalReportDTO> {
    return this.http.get<BiologicalReportDTO>(`${this.baseUrl}/latest/dto`, { params: { patientId } });
  }

  createReport(report: LabReport): Observable<LabReport> {
    return this.http.post<LabReport>(this.baseUrl, report);
  }

  updateReport(id: number, report: LabReport): Observable<LabReport> {
    return this.http.put<LabReport>(`${this.baseUrl}/${id}`, report);
  }

  deleteReport(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // -------- Results --------

  getResults(reportId: number): Observable<BiologicalResult[]> {
    return this.http.get<BiologicalResult[]>(`${this.baseUrl}/${reportId}/results`);
  }

  addResult(reportId: number, result: BiologicalResult): Observable<BiologicalResult> {
    return this.http.post<BiologicalResult>(`${this.baseUrl}/${reportId}/results`, result);
  }

  updateResult(reportId: number, resultId: number, result: BiologicalResult): Observable<BiologicalResult> {
    return this.http.put<BiologicalResult>(`${this.baseUrl}/${reportId}/results/${resultId}`, result);
  }

  deleteResult(reportId: number, resultId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${reportId}/results/${resultId}`);
  }

  // -------- Statistics --------

  getStatistics(from?: string, to?: string): Observable<LabReportStatistics> {
    const params: any = {};
    if (from) params.from = from;
    if (to) params.to = to;
    return this.http.get<LabReportStatistics>(`${this.baseUrl}/statistics`, { params });
  }

  // -------- 📅 CALENDAR (NEW) --------

  getCalendarEvents(): Observable<CalendarEvent[]> {
    return this.http.get<CalendarEvent[]>(`${this.baseUrl}/calendar`);
  }
}