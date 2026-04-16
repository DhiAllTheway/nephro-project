import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CalendarComponent } from './calendar.component';

describe('CalendarComponent', () => {
  let component: CalendarComponent;
  let fixture: ComponentFixture<CalendarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalendarComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(CalendarComponent);
    component = fixture.componentInstance;
  });

  it('should mark day as urgent when it has an URGENT event', () => {
    const day = {
      date: '2026-04-16',
      events: [
        {
          reportId: 1,
          patientId: 10,
          type: 'URGENT',
          date: '2026-04-16',
          label: 'Critical follow-up',
          severity: 5
        }
      ]
    };

    const result = component.isUrgent(day);

    expect(result).toBeTrue();
  });
});