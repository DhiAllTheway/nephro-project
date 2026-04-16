import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { LabResultsApiService } from '../../../shared/services/lab-results-api.service';
import { LabReport } from '../../../shared/models/lab-report.model';
import { BiologicalResult } from '../../../shared/models/biological-result.model';
import { BiologicalReportDTO } from '../../../shared/models/biological-report-dto.model';
import { BiologicalResultDTO } from '../../../shared/models/biological-result-dto.model';

@Component({
  selector: 'app-lab-report-details',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './lab-report-details.component.html',
  
})
export class LabReportDetailsComponent implements OnInit {

  reportId!: number;

  // ✅ DTO view
  reportDto?: BiologicalReportDTO;
  resultsDto: BiologicalResultDTO[] = [];

  // ✅ for add/edit requests (entity payload)
  showAddForm = false;
  editingResultId: number | null = null;

  newResult: BiologicalResult = this.emptyResult();
  editResult: BiologicalResult = this.emptyResult();

  loading = false;
  error?: string;

  constructor(
    private route: ActivatedRoute,
    private api: LabResultsApiService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id || Number.isNaN(id)) {
      this.error = 'Invalid report id';
      return;
    }

    this.reportId = id;
    this.loadDto();
  }

  private emptyResult(): BiologicalResult {
    return {
      parameterName: '',
      value: 0,
      unit: '',
      normalMinValue: undefined,
      normalMaxValue: undefined,
    };
  }

  private loadDto(): void {
    this.loading = true;
    this.error = undefined;

    this.api.getReportDtoById(this.reportId).subscribe({
      next: (dto) => {
        this.reportDto = dto;
        this.resultsDto = dto.results ?? [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to load report (DTO). Check lab-results-service + gateway routes.';
      }
    });
  }

  // ================= ADD =================

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    this.editingResultId = null;
    this.error = undefined;
  }

  addResult(): void {
    if (!this.newResult.parameterName?.trim()) {
      this.error = 'Parameter name is required.';
      return;
    }

    this.api.addResult(this.reportId, this.newResult).subscribe({
      next: () => {
        this.newResult = this.emptyResult();
        this.showAddForm = false;
        this.loadDto();
      },
      error: () => {
        this.error = 'Failed to add result.';
      },
    });
  }

  // ================= EDIT =================

  startEdit(result: BiologicalResultDTO): void {
    this.editingResultId = result.id ?? null;
    this.showAddForm = false;
    this.error = undefined;

    // map dto -> entity payload
    this.editResult = {
      id: result.id,
      parameterName: result.parameterName,
      value: result.value,
      unit: result.unit,
      normalMinValue: result.normalMinValue,
      normalMaxValue: result.normalMaxValue,
    };
  }

  cancelEdit(): void {
    this.editingResultId = null;
    this.editResult = this.emptyResult();
  }

  saveEdit(): void {
    if (!this.editingResultId) return;

    this.api.updateResult(this.reportId, this.editingResultId, this.editResult).subscribe({
      next: () => {
        this.editingResultId = null;
        this.loadDto();
      },
      error: () => {
        this.error = 'Failed to update result.';
      },
    });
  }

  // ================= DELETE =================

  deleteResult(resultId?: number): void {
    if (!resultId) return;

    this.api.deleteResult(this.reportId, resultId).subscribe({
      next: () => this.loadDto(),
      error: () => {
        this.error = 'Failed to delete result.';
      },
    });
  }

  // ================= UI HELPERS =================

  badgeClass(status: string): string {
    if (status === 'HIGH') return 'bg-red-100 text-red-800 border-red-200';
    if (status === 'LOW') return 'bg-yellow-100 text-yellow-900 border-yellow-200';
    return 'bg-green-100 text-green-800 border-green-200';
  }

  reportBadgeClass(reportStatus: string): string {
    return reportStatus === 'ABNORMAL'
      ? 'bg-red-100 text-red-800 border-red-200'
      : 'bg-green-100 text-green-800 border-green-200';
  }
}