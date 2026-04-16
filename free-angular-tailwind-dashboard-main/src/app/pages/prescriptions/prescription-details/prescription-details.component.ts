import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { PrescriptionsApiService } from '../../../shared/services/prescriptions-api.service';
import { Prescription } from '../../../shared/models/prescription.model';
import { PrescribedMedication } from '../../../shared/models/prescribed-medication.model';
import { ValidationResponse } from '../../../shared/models/validation-response.model';
import { AiFloatingAssistantComponent } from '../../../shared/components/ai-floating-assistant.component';

@Component({
  selector: 'app-prescription-details',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, AiFloatingAssistantComponent],
  templateUrl: './prescription-details.component.html',
})
export class PrescriptionDetailsComponent implements OnInit {
  prescription?: Prescription;
  medications: PrescribedMedication[] = [];

  validation?: ValidationResponse;
  validating = false;

  prescriptionId!: number;

  loading = false;
  error?: string;

  showAddForm = false;
  editingMedId: number | null = null;

  newMed: PrescribedMedication = this.emptyMed();
  editMed: PrescribedMedication = this.emptyMed();

  showSignPad = false;
  signedBy = '';
  signing = false;

  aiLoading = false;
  aiExplanation?: string;
  aiError?: string;

  @ViewChild('sigCanvas', { static: false }) sigCanvas?: ElementRef<HTMLCanvasElement>;
  private ctx?: CanvasRenderingContext2D | null;
  private drawing = false;

  constructor(
    private route: ActivatedRoute,
    private api: PrescriptionsApiService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id || Number.isNaN(id)) {
      this.error = 'Invalid prescription id';
      return;
    }

    this.prescriptionId = id;
    this.loadPrescription();
    this.loadMeds();
  }

  get isSigned(): boolean {
    return (this.prescription?.status ?? 'DRAFT') === 'SIGNED';
  }

  private emptyMed(): PrescribedMedication {
    return {
      medicationName: '',
      dosage: '',
      frequency: '',
      durationDays: undefined
    };
  }

  private loadPrescription(): void {
    this.loading = true;
    this.api.getPrescriptionById(this.prescriptionId).subscribe({
      next: (p) => {
        this.prescription = p;
        this.signedBy = p.doctorName ?? '';
        this.loading = false;
        this.refreshValidation();
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to load prescription.';
      }
    });
  }

  private loadMeds(): void {
    this.api.getMedications(this.prescriptionId).subscribe({
      next: (m) => this.medications = m,
      error: () => this.error = 'Failed to load medications.'
    });
  }

  refreshValidation(): void {
    this.validating = true;
    this.api.validatePrescription(this.prescriptionId).subscribe({
      next: (v) => {
        this.validation = v;
        this.validating = false;
      },
      error: () => {
        this.validating = false;
        this.validation = {
          level: 'WARN',
          message: 'Validation failed (service unavailable).',
          patientId: this.prescription?.patientId ?? 0
        };
      }
    });
  }

  validationClass(): string {
    if (!this.validation) return 'border-gray-200 bg-white';
    if (this.validation.level === 'ERROR') return 'border-red-200 bg-red-50';
    if (this.validation.level === 'WARN') return 'border-yellow-200 bg-yellow-50';
    return 'border-green-200 bg-green-50';
  }

  validationTitleClass(): string {
    if (!this.validation) return 'text-gray-900';
    if (this.validation.level === 'ERROR') return 'text-red-800';
    if (this.validation.level === 'WARN') return 'text-yellow-900';
    return 'text-green-800';
  }

  explainPrescription(): void {
    this.aiLoading = true;
    this.aiError = undefined;
    this.aiExplanation = undefined;

    this.api.explainPrescription(this.prescriptionId).subscribe({
      next: (res) => {
        this.aiExplanation = res.explanation;
        this.aiLoading = false;
      },
      error: () => {
        this.aiLoading = false;
        this.aiError = 'AI explanation failed. Make sure Ollama is running.';
      }
    });
  }

  toggleAddForm(): void {
    if (this.isSigned) return;
    this.showAddForm = !this.showAddForm;
    this.editingMedId = null;
  }

  addMedication(): void {
    if (this.isSigned) {
      this.error = 'Prescription is signed and cannot be modified.';
      return;
    }

    if (!this.newMed.medicationName.trim()) {
      this.error = 'Medication name is required.';
      return;
    }

    this.api.addMedication(this.prescriptionId, this.newMed).subscribe({
      next: () => {
        this.newMed = this.emptyMed();
        this.showAddForm = false;
        this.loadMeds();
      },
      error: (err) => {
        this.error = err?.error?.message || 'Failed to add medication.';
      }
    });
  }

  startEdit(m: PrescribedMedication): void {
    if (this.isSigned) return;
    this.editingMedId = m.id ?? null;
    this.showAddForm = false;
    this.editMed = { ...m };
  }

  cancelEdit(): void {
    this.editingMedId = null;
    this.editMed = this.emptyMed();
  }

  saveEdit(): void {
    if (this.isSigned) {
      this.error = 'Prescription is signed and cannot be modified.';
      return;
    }

    if (!this.editingMedId) return;

    this.api.updateMedication(this.prescriptionId, this.editingMedId, this.editMed).subscribe({
      next: () => {
        this.editingMedId = null;
        this.loadMeds();
      },
      error: (err) => {
        this.error = err?.error?.message || 'Failed to update medication.';
      }
    });
  }

  deleteMed(id?: number): void {
    if (this.isSigned) {
      this.error = 'Prescription is signed and cannot be modified.';
      return;
    }

    if (!id) return;

    this.api.deleteMedication(this.prescriptionId, id).subscribe({
      next: () => this.loadMeds(),
      error: (err) => {
        this.error = err?.error?.message || 'Failed to delete medication.';
      }
    });
  }

  openSignPad(): void {
    if (this.isSigned) return;
    this.showSignPad = true;
    this.error = undefined;
    setTimeout(() => this.initCanvas(), 0);
  }

  closeSignPad(): void {
    this.showSignPad = false;
    this.drawing = false;
  }

  private initCanvas(): void {
    const canvas = this.sigCanvas?.nativeElement;
    if (!canvas) return;

    const ratio = window.devicePixelRatio || 1;
    const width = canvas.clientWidth || 600;
    const height = canvas.clientHeight || 200;

    canvas.width = Math.floor(width * ratio);
    canvas.height = Math.floor(height * ratio);

    this.ctx = canvas.getContext('2d');
    if (this.ctx) {
      this.ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
      this.ctx.lineWidth = 2;
      this.ctx.lineCap = 'round';
      this.ctx.strokeStyle = '#111827';
      this.clearSignature();
    }
  }

  clearSignature(): void {
    const canvas = this.sigCanvas?.nativeElement;
    if (!canvas || !this.ctx) return;

    const w = canvas.width / (window.devicePixelRatio || 1);
    const h = canvas.height / (window.devicePixelRatio || 1);
    this.ctx.clearRect(0, 0, w, h);
  }

  onPointerDown(ev: PointerEvent): void {
    if (!this.ctx) return;
    this.drawing = true;
    const { x, y } = this.getCanvasPoint(ev);
    this.ctx.beginPath();
    this.ctx.moveTo(x, y);
  }

  onPointerMove(ev: PointerEvent): void {
    if (!this.ctx || !this.drawing) return;
    const { x, y } = this.getCanvasPoint(ev);
    this.ctx.lineTo(x, y);
    this.ctx.stroke();
  }

  onPointerUp(): void {
    this.drawing = false;
  }

  private getCanvasPoint(ev: PointerEvent): { x: number; y: number } {
    const canvas = this.sigCanvas!.nativeElement;
    const rect = canvas.getBoundingClientRect();
    return {
      x: ev.clientX - rect.left,
      y: ev.clientY - rect.top
    };
  }

  submitSignature(): void {
    if (this.isSigned) return;

    const canvas = this.sigCanvas?.nativeElement;
    if (!canvas) return;

    const signatureData = canvas.toDataURL('image/png');

    if (!this.hasInk(canvas)) {
      this.error = 'Please provide a signature before signing.';
      return;
    }

    this.signing = true;
    this.api.signPrescription(this.prescriptionId, {
      signatureData,
      signedBy: this.signedBy
    }).subscribe({
      next: (p) => {
        this.prescription = p;
        this.signing = false;
        this.showSignPad = false;
        this.editingMedId = null;
        this.showAddForm = false;
      },
      error: (err) => {
        this.signing = false;
        this.error = err?.error?.message || 'Failed to sign prescription.';
      }
    });
  }

  private hasInk(canvas: HTMLCanvasElement): boolean {
    const ctx = canvas.getContext('2d');
    if (!ctx) return false;
    const { data } = ctx.getImageData(0, 0, canvas.width, canvas.height);
    for (let i = 3; i < data.length; i += 4) {
      if (data[i] !== 0) return true;
    }
    return false;
  }
}