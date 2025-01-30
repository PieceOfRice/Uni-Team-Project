import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../overwatch-map.test-samples';

import { OverwatchMapFormService } from './overwatch-map-form.service';

describe('OverwatchMap Form Service', () => {
  let service: OverwatchMapFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OverwatchMapFormService);
  });

  describe('Service methods', () => {
    describe('createOverwatchMapFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOverwatchMapFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            mode: expect.any(Object),
          })
        );
      });

      it('passing IOverwatchMap should create a new form with FormGroup', () => {
        const formGroup = service.createOverwatchMapFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            mode: expect.any(Object),
          })
        );
      });
    });

    describe('getOverwatchMap', () => {
      it('should return NewOverwatchMap for default OverwatchMap initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createOverwatchMapFormGroup(sampleWithNewData);

        const overwatchMap = service.getOverwatchMap(formGroup) as any;

        expect(overwatchMap).toMatchObject(sampleWithNewData);
      });

      it('should return NewOverwatchMap for empty OverwatchMap initial value', () => {
        const formGroup = service.createOverwatchMapFormGroup();

        const overwatchMap = service.getOverwatchMap(formGroup) as any;

        expect(overwatchMap).toMatchObject({});
      });

      it('should return IOverwatchMap', () => {
        const formGroup = service.createOverwatchMapFormGroup(sampleWithRequiredData);

        const overwatchMap = service.getOverwatchMap(formGroup) as any;

        expect(overwatchMap).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOverwatchMap should not enable id FormControl', () => {
        const formGroup = service.createOverwatchMapFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOverwatchMap should disable id FormControl', () => {
        const formGroup = service.createOverwatchMapFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
