import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LabResultsApiService } from '../../shared/services/lab-results-api.service';
import { CalendarEvent } from '../../shared/models/calendar-event.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './calendar.component.html',
})
export class CalendarComponent implements OnInit {

  events: CalendarEvent[] = [];
  days: any[] = [];

  currentDate = new Date();
  today = new Date().toISOString().split('T')[0];

  constructor(
    private api: LabResultsApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.api.getCalendarEvents().subscribe(res => {
      this.events = res;
      this.generateCalendar();
    });
  }

  // =========================
  // 📅 GENERATE CALENDAR
  // =========================
  generateCalendar() {

    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();

    const firstDay = new Date(year, month, 1).getDay();
    const totalDays = new Date(year, month + 1, 0).getDate();

    this.days = [];

    // Empty slots
    for (let i = 0; i < firstDay; i++) {
      this.days.push(null);
    }

    // Real days
    for (let d = 1; d <= totalDays; d++) {
      const dateStr = new Date(year, month, d).toISOString().split('T')[0];

      const dayEvents = this.events.filter(e => e.date === dateStr);

      this.days.push({
        day: d,
        date: dateStr,
        events: dayEvents
      });
    }
  }

  // =========================
  // 📌 NAVIGATION
  // =========================

  prevMonth() {
    this.currentDate = new Date(
      this.currentDate.getFullYear(),
      this.currentDate.getMonth() - 1,
      1
    );
    this.generateCalendar();
  }

  nextMonth() {
    this.currentDate = new Date(
      this.currentDate.getFullYear(),
      this.currentDate.getMonth() + 1,
      1
    );
    this.generateCalendar();
  }

  getMonthYear(): string {
    return this.currentDate.toLocaleString('default', {
      month: 'long',
      year: 'numeric'
    });
  }

  // =========================
  // 🎯 HELPERS
  // =========================

  openEvent(event: CalendarEvent) {
    this.router.navigate(['/lab-reports', event.reportId]);
  }

  isToday(day: any) {
    return day?.date === this.today;
  }

  isUrgent(day: any) {
    return day?.events?.some((e: CalendarEvent) => e.type === 'URGENT');
  }
}