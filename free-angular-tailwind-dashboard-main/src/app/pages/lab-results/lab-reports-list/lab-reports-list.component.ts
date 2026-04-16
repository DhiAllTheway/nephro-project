import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LabResultsApiService, PageResponse } from '../../../shared/services/lab-results-api.service';
import { LabReport } from '../../../shared/models/lab-report.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-lab-reports-list',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './lab-reports-list.component.html',
  
})
export class LabReportsListComponent implements OnInit {

  // Filters
  patientId?: number;
  date?: string;

  // Pagination
  page = 0;
  size = 10;

  // Data from backend
  pageData?: PageResponse<LabReport>;
  loading = false;
  error?: string;

  // Delete state
  deletingId?: number;

  constructor(private api: LabResultsApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = undefined;

    this.api.getReports(this.patientId, this.date, this.page, this.size).subscribe({
      next: (res) => {
        this.pageData = res;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Failed to load reports. Is Spring Boot running on port 9003?';
        console.error(err);
      }
    });
  }

  applyFilters(): void {
    this.page = 0;
    this.load();
  }

  resetFilters(): void {
    this.patientId = undefined;
    this.date = undefined;
    this.applyFilters();
  }

  nextPage(): void {
    if (!this.pageData) return;
    if (this.page + 1 >= this.pageData.totalPages) return;
    this.page++;
    this.load();
  }

  prevPage(): void {
    if (this.page <= 0) return;
    this.page--;
    this.load();
  }

  deleteReport(id: number): void {
    const ok = confirm(`Delete report #${id}?`);
    if (!ok) return;

    this.deletingId = id;
    this.error = undefined;

    this.api.deleteReport(id).subscribe({
      next: () => {
        this.deletingId = undefined;

        // If we deleted the last item of the current page, go back a page if possible
        const currentCount = this.pageData?.content?.length ?? 0;
        if (currentCount === 1 && this.page > 0) {
          this.page--;
        }

        this.load();
      },
      error: (err) => {
        this.deletingId = undefined;
        this.error = 'Failed to delete report.';
        console.error(err);
      }
    });
  }
}
