import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PrescriptionsApiService } from '../services/prescriptions-api.service';

@Component({
  selector: 'app-ai-floating-assistant',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="fixed bottom-6 right-6 z-[9999]">
      @if (!isOpen) {
        <button
          (click)="toggleOpen()"
          class="group flex items-center gap-3 rounded-full bg-blue-600 px-5 py-4 text-white shadow-2xl transition-all duration-300 hover:scale-105 hover:bg-blue-700"
        >
          <span class="flex h-10 w-10 items-center justify-center rounded-full bg-white/20 text-xl">
            💬
          </span>
          <span class="hidden text-sm font-semibold md:block">
            AI Assistant
          </span>
        </button>
      }

      @if (isOpen) {
        <div
          class="w-[360px] overflow-hidden rounded-3xl border border-blue-200 bg-white shadow-2xl dark:border-blue-900 dark:bg-gray-900"
        >
          <div class="bg-gradient-to-r from-blue-600 to-indigo-600 p-5 text-white">
            <div class="flex items-start justify-between gap-3">
              <div>
                <div class="text-lg font-semibold">AI Assistant for Elderly</div>
                <div class="mt-1 text-sm text-blue-100">
                  Simple explanation for clinic staff and patients
                </div>
              </div>

              <button
                (click)="toggleOpen()"
                class="rounded-full bg-white/15 px-3 py-1 text-sm font-medium hover:bg-white/25"
              >
                ✕
              </button>
            </div>
          </div>

          <div class="space-y-4 p-5">
            <div class="rounded-2xl border border-gray-200 bg-gray-50 p-4 dark:border-gray-800 dark:bg-white/[0.03]">
              <div class="text-sm font-medium text-gray-900 dark:text-white">
                Current prescription
              </div>
              <div class="mt-1 text-sm text-gray-600 dark:text-gray-300">
                ID #{{ prescriptionId }}
              </div>
            </div>

            <div class="flex gap-2">
              <button
                (click)="explainPrescription()"
                [disabled]="loading"
                class="flex-1 rounded-xl bg-blue-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:opacity-50"
              >
                {{ loading ? 'Explaining...' : 'Explain Prescription' }}
              </button>

              <button
                (click)="clearResult()"
                class="rounded-xl border border-gray-200 px-4 py-3 text-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-200 dark:hover:bg-white/[0.03]"
              >
                Clear
              </button>
            </div>

            @if (loading) {
              <div class="rounded-2xl border border-blue-200 bg-blue-50 p-4 text-sm text-blue-800 dark:border-blue-900 dark:bg-blue-950/20 dark:text-blue-200">
                AI is preparing a simplified explanation...
              </div>
            }

            @if (error) {
              <div class="rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700 dark:border-red-900 dark:bg-red-950/20 dark:text-red-200">
                {{ error }}
              </div>
            }

            @if (explanation) {
              <div class="rounded-2xl border border-blue-200 bg-blue-50/60 p-4 dark:border-blue-900 dark:bg-blue-950/10">
                <div class="mb-2 text-xs font-semibold uppercase tracking-wide text-blue-700 dark:text-blue-300">
                  Simplified explanation
                </div>

                <div class="max-h-[280px] overflow-y-auto whitespace-pre-line text-sm leading-7 text-gray-800 dark:text-gray-100">
                  {{ explanation }}
                </div>

                <div class="mt-4 rounded-xl border border-amber-200 bg-amber-50 px-3 py-2 text-xs text-amber-800 dark:border-amber-900 dark:bg-amber-950/20 dark:text-amber-200">
                  This explanation is simplified and does not replace the doctor or pharmacist.
                </div>
              </div>
            }
          </div>
        </div>
      }
    </div>
  `,
})
export class AiFloatingAssistantComponent {
  @Input() prescriptionId!: number;

  isOpen = false;
  loading = false;
  explanation?: string;
  error?: string;

  constructor(private api: PrescriptionsApiService) {}

  toggleOpen(): void {
    this.isOpen = !this.isOpen;
  }

  clearResult(): void {
    this.explanation = undefined;
    this.error = undefined;
  }

  explainPrescription(): void {
    if (!this.prescriptionId) {
      this.error = 'No prescription selected.';
      return;
    }

    this.loading = true;
    this.error = undefined;
    this.explanation = undefined;

    this.api.explainPrescription(this.prescriptionId).subscribe({
      next: (res) => {
        this.explanation = res.explanation;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.error = 'AI explanation failed. Make sure Ollama is running.';
      }
    });
  }
}