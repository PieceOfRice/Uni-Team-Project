import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tournament-accessibility.test-samples';

import { TournamentAccessibilityFormService } from './tournament-accessibility-form.service';

describe('TournamentAccessibility Form Service', () => {
  let service: TournamentAccessibilityFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TournamentAccessibilityFormService);
  });

  describe('Service methods', () => {
    describe('createTournamentAccessibilityFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTournamentAccessibilityFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            accessibility: expect.any(Object),
            tournament: expect.any(Object),
          })
        );
      });

      it('passing ITournamentAccessibility should create a new form with FormGroup', () => {
        const formGroup = service.createTournamentAccessibilityFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            accessibility: expect.any(Object),
            tournament: expect.any(Object),
          })
        );
      });
    });

    describe('getTournamentAccessibility', () => {
      it('should return NewTournamentAccessibility for default TournamentAccessibility initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTournamentAccessibilityFormGroup(sampleWithNewData);

        const tournamentAccessibility = service.getTournamentAccessibility(formGroup) as any;

        expect(tournamentAccessibility).toMatchObject(sampleWithNewData);
      });

      it('should return NewTournamentAccessibility for empty TournamentAccessibility initial value', () => {
        const formGroup = service.createTournamentAccessibilityFormGroup();

        const tournamentAccessibility = service.getTournamentAccessibility(formGroup) as any;

        expect(tournamentAccessibility).toMatchObject({});
      });

      it('should return ITournamentAccessibility', () => {
        const formGroup = service.createTournamentAccessibilityFormGroup(sampleWithRequiredData);

        const tournamentAccessibility = service.getTournamentAccessibility(formGroup) as any;

        expect(tournamentAccessibility).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITournamentAccessibility should not enable id FormControl', () => {
        const formGroup = service.createTournamentAccessibilityFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTournamentAccessibility should disable id FormControl', () => {
        const formGroup = service.createTournamentAccessibilityFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
