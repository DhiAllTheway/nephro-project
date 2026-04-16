export interface DailyPoint {
  date: string;
  abnormalReports: number;
  totalReports: number;
}

export interface PatientRisk {
  patientId: number;
  totalReports: number;
  abnormalReports: number;
  maxSeverity: number;
  riskScore: number;
}

export interface LabReportStatistics {
  totalReports: number;
  normalReports: number;
  abnormalReports: number;
  abnormalRate: number; // 0..100
  avgSeverity: number;

  statusDistribution: Record<string, number>;

  dailyAbnormalReports: DailyPoint[];
  topRiskPatients: PatientRisk[];
}