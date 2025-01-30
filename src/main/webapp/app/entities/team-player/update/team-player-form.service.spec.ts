import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../team-player.test-samples';

import { TeamPlayerFormService } from './team-player-form.service';

describe('TeamPlayer Form Service', () => {
  let service: TeamPlayerFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamPlayerFormService);
  });

  describe('Service methods', () => {
    describe('createTeamPlayerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTeamPlayerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            role: expect.any(Object),
            player: expect.any(Object),
            team: expect.any(Object),
          })
        );
      });

      it('passing ITeamPlayer should create a new form with FormGroup', () => {
        const formGroup = service.createTeamPlayerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            role: expect.any(Object),
            player: expect.any(Object),
            team: expect.any(Object),
          })
        );
      });
    });

    describe('getTeamPlayer', () => {
      it('should return NewTeamPlayer for default TeamPlayer initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTeamPlayerFormGroup(sampleWithNewData);

        const teamPlayer = service.getTeamPlayer(formGroup) as any;

        expect(teamPlayer).toMatchObject(sampleWithNewData);
      });

      it('should return NewTeamPlayer for empty TeamPlayer initial value', () => {
        const formGroup = service.createTeamPlayerFormGroup();

        const teamPlayer = service.getTeamPlayer(formGroup) as any;

        expect(teamPlayer).toMatchObject({});
      });

      it('should return ITeamPlayer', () => {
        const formGroup = service.createTeamPlayerFormGroup(sampleWithRequiredData);

        const teamPlayer = service.getTeamPlayer(formGroup) as any;

        expect(teamPlayer).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITeamPlayer should not enable id FormControl', () => {
        const formGroup = service.createTeamPlayerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTeamPlayer should disable id FormControl', () => {
        const formGroup = service.createTeamPlayerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
