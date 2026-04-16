import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { LabResultsApiService } from '../../../shared/services/lab-results-api.service';
import { LabReport } from '../../../shared/models/lab-report.model';

@Component({
  selector: 'app-lab-report-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './lab-report-form.component.html',
})
export class LabReportFormComponent implements OnInit {

  // if id exists -> edit mode, else -> create mode
  id?: number;
  isEditMode = false;

  // form model
  form: Partial<LabReport> = {
    patientId: 0,
    reportDate: '',
    analysisType: '',
    laboratoryName: '',
    comment: ''
  };

  loading = false;
  error?: string;

  constructor(
    private api: LabResultsApiService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.id = Number(idParam);
      this.isEditMode = true;
      this.loadForEdit(this.id);
    }
  }

  private loadForEdit(id: number): void {
    this.loading = true;
    this.error = undefined;

    this.api.getReportById(id).subscribe({
      next: (report) => {
        // copy backend data into form
        this.form = { ...report };
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Failed to load report for editing.';
        console.error(err);
      }
    });
  }

  save(): void {
    this.loading = true;
    this.error = undefined;

    if (this.isEditMode && this.id != null) {
      // EDIT (PUT)
      this.api.updateReport(this.id, this.form as LabReport).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/lab-reports', this.id]);
        },
        error: (err) => {
          this.loading = false;
          this.error = 'Failed to update report.';
          console.error(err);
        }
      });
    } else {
      // CREATE (POST)
      this.api.createReport(this.form as LabReport).subscribe({
        next: (created) => {
          this.loading = false;
          this.router.navigate(['/lab-reports']);
        },
        error: (err) => {
          this.loading = false;
          this.error = 'Failed to create report.';
          console.error(err);
        }
      });
    }
  }
}
