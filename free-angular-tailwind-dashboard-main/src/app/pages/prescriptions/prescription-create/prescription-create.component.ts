import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { PrescriptionsApiService } from '../../../shared/services/prescriptions-api.service';
import { Prescription } from '../../../shared/models/prescription.model';

@Component({
  selector: 'app-prescription-create',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './prescription-create.component.html',
})
export class PrescriptionCreateComponent {
  prescription: Prescription = {
    patientId: 1,
    prescriptionDate: new Date().toISOString().slice(0, 10),
    doctorName: '',
    notes: ''
  };

  loading = false;
  error?: string;

  constructor(private api: PrescriptionsApiService, private router: Router) {}

  save(): void {
    if (!this.prescription.doctorName.trim()) {
      this.error = 'Doctor name is required.';
      return;
    }

    this.loading = true;
    this.api.createPrescription(this.prescription).subscribe({
      next: (created) => {
        this.loading = false;
        this.router.navigate(['/prescriptions', created.id]);
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to create prescription.';
      }
    });
  }
}