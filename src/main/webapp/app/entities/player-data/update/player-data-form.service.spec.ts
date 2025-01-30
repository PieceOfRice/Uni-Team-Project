import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../player-data.test-samples';

import { PlayerDataFormService } from './player-data-form.service';

describe('PlayerData Form Service', () => {
  let service: PlayerDataFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlayerDataFormService);
  });

  describe('Service methods', () => {
    describe('createPlayerDataFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPlayerDataFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            overwatchUsername: expect.any(Object),
            profile: expect.any(Object),
            bio: expect.any(Object),
            primaryLanguage: expect.any(Object),
            device: expect.any(Object),
            matchesPlayed: expect.any(Object),
            tournamentsPlayed: expect.any(Object),
            matchWins: expect.any(Object),
            tournamentWins: expect.any(Object),
            tournamentTop10s: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IPlayerData should create a new form with FormGroup', () => {
        const formGroup = service.createPlayerDataFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            overwatchUsername: expect.any(Object),
            profile: expect.any(Object),
            bio: expect.any(Object),
            primaryLanguage: expect.any(Object),
            device: expect.any(Object),
            matchesPlayed: expect.any(Object),
            tournamentsPlayed: expect.any(Object),
            matchWins: expect.any(Object),
            tournamentWins: expect.any(Object),
            tournamentTop10s: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getPlayerData', () => {
      it('should return NewPlayerData for default PlayerData initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPlayerDataFormGroup(sampleWithNewData);

        const playerData = service.getPlayerData(formGroup) as any;

        expect(playerData).toMatchObject(sampleWithNewData);
      });

      it('should return NewPlayerData for empty PlayerData initial value', () => {
        const formGroup = service.createPlayerDataFormGroup();

        const playerData = service.getPlayerData(formGroup) as any;

        expect(playerData).toMatchObject({});
      });

      it('should return IPlayerData', () => {
        const formGroup = service.createPlayerDataFormGroup(sampleWithRequiredData);

        const playerData = service.getPlayerData(formGroup) as any;

        expect(playerData).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPlayerData should not enable id FormControl', () => {
        const formGroup = service.createPlayerDataFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPlayerData should disable id FormControl', () => {
        const formGroup = service.createPlayerDataFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
