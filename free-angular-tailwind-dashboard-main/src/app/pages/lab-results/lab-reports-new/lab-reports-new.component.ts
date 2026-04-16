import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LabResultsApiService } from '../../../shared/services/lab-results-api.service';
import { LabReport } from '../../../shared/models/lab-report.model';

@Component({
  selector: 'app-lab-reports-new',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './lab-reports-new.component.html',
})
export class LabReportsNewComponent {
  // Matches backend DTO/entity fields (adjust names if your backend differs)
  form: Partial<LabReport> = {
    patientId: undefined,
    reportDate: '',       // "YYYY-MM-DD"
    analysisType: '',
    laboratoryName: '',
    comment: '',
  };

  loading = false;
  error?: string;

  constructor(
    private api: LabResultsApiService,
    private router: Router
  ) {}

  save(): void {
    this.error = undefined;

    // Basic front validation
    if (!this.form.patientId || !this.form.reportDate || !this.form.analysisType || !this.form.laboratoryName) {
      this.error = 'Please fill Patient ID, Date, Type, and Lab.';
      return;
    }

    this.loading = true;

    this.api.createReport(this.form as LabReport).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/lab-reports']);
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Failed to save report. Check Spring Boot on http://localhost:9003 and CORS.';
        console.error(err);
      },
    });
  }
}
