import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { PrescriptionDetailsComponent } from './prescription-details.component';

describe('PrescriptionDetailsComponent', () => {
  let component: PrescriptionDetailsComponent;
  let fixture: ComponentFixture<PrescriptionDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrescriptionDetailsComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ id: '1' })
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PrescriptionDetailsComponent);
    component = fixture.componentInstance;
  });

  it('should block medication changes when prescription is signed', () => {
    component.prescription = {
      id: 1,
      patientId: 10,
      prescriptionDate: '2026-04-16',
      doctorName: 'Dr Test',
      status: 'SIGNED'
    };

    component.newMed = {
      medicationName: 'Paracetamol',
      dosage: '500mg',
      frequency: '2/day',
      durationDays: 5
    };

    component.addMedication();

    expect(component.error).toBe('Prescription is signed and cannot be modified.');
  });
  it('should refuse signing when signature pad is empty', () => {
  component.prescription = {
    id: 1,
    patientId: 10,
    prescriptionDate: '2026-04-16',
    doctorName: 'Dr Test',
    status: 'DRAFT'
  };

  const fakeCanvas = {
    toDataURL: () => 'data:image/png;base64,fake'
  } as HTMLCanvasElement;

  component.sigCanvas = {
    nativeElement: fakeCanvas
  } as any;

  spyOn<any>(component, 'hasInk').and.returnValue(false);

  component.submitSignature();

  expect(component.error).toBe('Please provide a signature before signing.');
});
});