import { CommonModule } from '@angular/common';
import { Component, computed, signal } from '@angular/core';

import { LabResultsApiService } from '../../../shared/services/lab-results-api.service';
import { LabReportStatistics } from '../../../shared/models/lab-report-statistics.model';

type DailyPoint = { date: string; abnormalReports: number; totalReports: number };
type RiskPatient = {
  patientId: number;
  totalReports: number;
  abnormalReports: number;
  maxSeverity: number;
  riskScore: number;
};

@Component({
  selector: 'app-lab-reports-statistics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './lab-reports-statistics.component.html',
})
export class LabReportsStatisticsComponent {
  loading = signal(true);
  error = signal<string | null>(null);
  stats = signal<LabReportStatistics | null>(null);

  // if empty => backend defaults last 30 days
  from = signal<string>('');
  to = signal<string>('');

  statusEntries = computed(() => {
    const s = this.stats();
    if (!s?.statusDistribution) return [];
    return Object.entries(s.statusDistribution);
  });

  trend = computed(() => {
    const s = this.stats() as any;
    const arr: DailyPoint[] = (s?.dailyAbnormalReports || []) as DailyPoint[];
    return arr;
  });

  maxTrend = computed(() => {
    const arr = this.trend();
    if (!arr.length) return 1;
    const max = Math.max(...arr.map((d: DailyPoint) => Number(d.abnormalReports || 0)), 1);
    return max;
  });

  topPatients = computed(() => {
    const s = this.stats() as any;
    const arr: RiskPatient[] = (s?.topRiskPatients || []) as RiskPatient[];
    return [...arr].sort((a, b) => (b.riskScore || 0) - (a.riskScore || 0));
  });

  constructor(private api: LabResultsApiService) {
    this.refresh();
  }

  refresh() {
    this.loading.set(true);
    this.error.set(null);

    this.api.getStatistics(this.from() || undefined, this.to() || undefined).subscribe({
      next: (data: LabReportStatistics) => {
        this.stats.set(data);
        this.loading.set(false);
      },
      error: (e: any) => {
        this.error.set(e?.error?.message || 'Failed to load statistics');
        this.loading.set(false);
      },
    });
  }

  onFromChange(value: string) {
    this.from.set(value || '');
  }

  onToChange(value: string) {
    this.to.set(value || '');
  }

  clearDates() {
    this.from.set('');
    this.to.set('');
    this.refresh();
  }

  setLastDays(days: number) {
    const today = new Date();
    const to = this.toIsoDate(today);

    const fromDate = new Date(today);
    fromDate.setDate(fromDate.getDate() - days);
    const from = this.toIsoDate(fromDate);

    this.from.set(from);
    this.to.set(to);
    this.refresh();
  }

  barWidth(value: number, max: number): string {
    const safeMax = max <= 0 ? 1 : max;
    const pct = Math.round((value * 100) / safeMax);
    return `${Math.min(Math.max(pct, 0), 100)}%`;
  }

  formatPct(n: any): string {
    const v = Number(n);
    if (Number.isNaN(v)) return '0%';
    return `${v.toFixed(1)}%`;
  }

  private toIsoDate(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }
}