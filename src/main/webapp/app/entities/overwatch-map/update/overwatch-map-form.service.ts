import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IOverwatchMap, NewOverwatchMap } from '../overwatch-map.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOverwatchMap for edit and NewOverwatchMapFormGroupInput for create.
 */
type OverwatchMapFormGroupInput = IOverwatchMap | PartialWithRequiredKeyOf<NewOverwatchMap>;

type OverwatchMapFormDefaults = Pick<NewOverwatchMap, 'id'>;

type OverwatchMapFormGroupContent = {
  id: FormControl<IOverwatchMap['id'] | NewOverwatchMap['id']>;
  name: FormControl<IOverwatchMap['name']>;
  mode: FormControl<IOverwatchMap['mode']>;
};

export type OverwatchMapFormGroup = FormGroup<OverwatchMapFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OverwatchMapFormService {
  createOverwatchMapFormGroup(overwatchMap: OverwatchMapFormGroupInput = { id: null }): OverwatchMapFormGroup {
    const overwatchMapRawValue = {
      ...this.getFormDefaults(),
      ...overwatchMap,
    };
    return new FormGroup<OverwatchMapFormGroupContent>({
      id: new FormControl(
        { value: overwatchMapRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(overwatchMapRawValue.name, {
        validators: [Validators.required],
      }),
      mode: new FormControl(overwatchMapRawValue.mode, {
        validators: [Validators.required],
      }),
    });
  }

  getOverwatchMap(form: OverwatchMapFormGroup): IOverwatchMap | NewOverwatchMap {
    return form.getRawValue() as IOverwatchMap | NewOverwatchMap;
  }

  resetForm(form: OverwatchMapFormGroup, overwatchMap: OverwatchMapFormGroupInput): void {
    const overwatchMapRawValue = { ...this.getFormDefaults(), ...overwatchMap };
    form.reset(
      {
        ...overwatchMapRawValue,
        id: { value: overwatchMapRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OverwatchMapFormDefaults {
    return {
      id: null,
    };
  }
}
