import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { PrescriptionsApiService, PageResponse } from '../../../shared/services/prescriptions-api.service';
import { Prescription } from '../../../shared/models/prescription.model';

@Component({
  selector: 'app-prescription-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './prescription-list.component.html',
})
export class PrescriptionListComponent implements OnInit {
  prescriptions: Prescription[] = [];

  patientId?: number;
  date?: string;

  page = 0;
  size = 10;
  totalPages = 0;

  loading = false;
  error?: string;

  constructor(private api: PrescriptionsApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = undefined;

    this.api.getPrescriptions(this.patientId, this.date, this.page, this.size).subscribe({
      next: (res: PageResponse<Prescription>) => {
        this.prescriptions = res.content;
        this.totalPages = res.totalPages;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to load prescriptions. Is Spring Boot running on port 9004?';
      }
    });
  }

  search(): void {
    this.page = 0;
    this.load();
  }

  reset(): void {
    this.patientId = undefined;
    this.date = undefined;
    this.page = 0;
    this.load();
  }

  prev(): void {
    if (this.page > 0) {
      this.page--;
      this.load();
    }
  }

  next(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.load();
    }
  }

  delete(id?: number): void {
    if (!id) return;
    if (!confirm('Delete this prescription?')) return;

    this.api.deletePrescription(id).subscribe({
      next: () => this.load(),
      error: () => this.error = 'Failed to delete prescription.'
    });
  }
}