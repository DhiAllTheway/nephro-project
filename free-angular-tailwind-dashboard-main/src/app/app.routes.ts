import { Routes } from '@angular/router';

import { AppLayoutComponent } from './shared/layout/app-layout/app-layout.component';

import { EcommerceComponent } from './pages/dashboard/ecommerce/ecommerce.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { FormElementsComponent } from './pages/forms/form-elements/form-elements.component';
import { BasicTablesComponent } from './pages/tables/basic-tables/basic-tables.component';
import { BlankComponent } from './pages/blank/blank.component';
import { InvoicesComponent } from './pages/invoices/invoices.component';
import { LineChartComponent } from './pages/charts/line-chart/line-chart.component';
import { BarChartComponent } from './pages/charts/bar-chart/bar-chart.component';

import { AlertsComponent } from './pages/ui-elements/alerts/alerts.component';
import { AvatarElementComponent } from './pages/ui-elements/avatar-element/avatar-element.component';
import { BadgesComponent } from './pages/ui-elements/badges/badges.component';
import { ButtonsComponent } from './pages/ui-elements/buttons/buttons.component';
import { ImagesComponent } from './pages/ui-elements/images/images.component';
import { VideosComponent } from './pages/ui-elements/videos/videos.component';

import { SignInComponent } from './pages/auth-pages/sign-in/sign-in.component';
import { SignUpComponent } from './pages/auth-pages/sign-up/sign-up.component';
import { NotFoundComponent } from './pages/other-page/not-found/not-found.component';

// ✅ LAB RESULTS
import { LabReportsListComponent } from './pages/lab-results/lab-reports-list/lab-reports-list.component';
import { LabReportFormComponent } from './pages/lab-results/lab-report-form/lab-report-form.component';
import { LabReportDetailsComponent } from './pages/lab-results/lab-report-details/lab-report-details.component';
import { LabReportsStatisticsComponent } from './pages/lab-results/lab-reports-statistics/lab-reports-statistics.component';

// ✅ PRESCRIPTIONS
import { PrescriptionListComponent } from './pages/prescriptions/prescription-list/prescription-list.component';
import { PrescriptionCreateComponent } from './pages/prescriptions/prescription-create/prescription-create.component';
import { PrescriptionDetailsComponent } from './pages/prescriptions/prescription-details/prescription-details.component';

// ✅ NEW CALENDAR (YOUR PAGE)
import { CalendarComponent } from './pages/calendar/calendar.component';

export const routes: Routes = [
  {
    path: '',
    component: AppLayoutComponent,
    children: [

      { path: '', component: EcommerceComponent },

      // 🔥 YOUR NEW CALENDAR (IMPORTANT)
      {
        path: 'medical-calendar',
        component: CalendarComponent,
        title: 'Medical Calendar',
      },

      // ---- EXISTING ----

      { path: 'profile', component: ProfileComponent },
      { path: 'form-elements', component: FormElementsComponent },
      { path: 'basic-tables', component: BasicTablesComponent },
      { path: 'blank', component: BlankComponent },
      { path: 'invoice', component: InvoicesComponent },
      { path: 'line-chart', component: LineChartComponent },
      { path: 'bar-chart', component: BarChartComponent },
      { path: 'alerts', component: AlertsComponent },
      { path: 'avatars', component: AvatarElementComponent },
      { path: 'badge', component: BadgesComponent },
      { path: 'buttons', component: ButtonsComponent },
      { path: 'images', component: ImagesComponent },
      { path: 'videos', component: VideosComponent },

      // LAB REPORTS
      { path: 'lab-reports', component: LabReportsListComponent },
      { path: 'lab-reports/new', component: LabReportFormComponent },
      { path: 'lab-reports/:id/edit', component: LabReportFormComponent },

      {
        path: 'lab-reports/statistics',
        component: LabReportsStatisticsComponent,
      },

      { path: 'lab-reports/:id', component: LabReportDetailsComponent },

      // PRESCRIPTIONS
      { path: 'prescriptions', component: PrescriptionListComponent },
      { path: 'prescriptions/new', component: PrescriptionCreateComponent },
      { path: 'prescriptions/:id', component: PrescriptionDetailsComponent },
    ],
  },

  { path: 'signin', component: SignInComponent },
  { path: 'signup', component: SignUpComponent },
  { path: '**', component: NotFoundComponent },
];