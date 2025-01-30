import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITournamentAccessibility, NewTournamentAccessibility } from '../tournament-accessibility.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITournamentAccessibility for edit and NewTournamentAccessibilityFormGroupInput for create.
 */
type TournamentAccessibilityFormGroupInput = ITournamentAccessibility | PartialWithRequiredKeyOf<NewTournamentAccessibility>;

type TournamentAccessibilityFormDefaults = Pick<NewTournamentAccessibility, 'id'>;

type TournamentAccessibilityFormGroupContent = {
  id: FormControl<ITournamentAccessibility['id'] | NewTournamentAccessibility['id']>;
  accessibility: FormControl<ITournamentAccessibility['accessibility']>;
  tournament: FormControl<ITournamentAccessibility['tournament']>;
};

export type TournamentAccessibilityFormGroup = FormGroup<TournamentAccessibilityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TournamentAccessibilityFormService {
  createTournamentAccessibilityFormGroup(
    tournamentAccessibility: TournamentAccessibilityFormGroupInput = { id: null }
  ): TournamentAccessibilityFormGroup {
    const tournamentAccessibilityRawValue = {
      ...this.getFormDefaults(),
      ...tournamentAccessibility,
    };
    return new FormGroup<TournamentAccessibilityFormGroupContent>({
      id: new FormControl(
        { value: tournamentAccessibilityRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      accessibility: new FormControl(tournamentAccessibilityRawValue.accessibility, {
        validators: [Validators.required],
      }),
      tournament: new FormControl(tournamentAccessibilityRawValue.tournament),
    });
  }

  getTournamentAccessibility(form: TournamentAccessibilityFormGroup): ITournamentAccessibility | NewTournamentAccessibility {
    return form.getRawValue() as ITournamentAccessibility | NewTournamentAccessibility;
  }

  resetForm(form: TournamentAccessibilityFormGroup, tournamentAccessibility: TournamentAccessibilityFormGroupInput): void {
    const tournamentAccessibilityRawValue = { ...this.getFormDefaults(), ...tournamentAccessibility };
    form.reset(
      {
        ...tournamentAccessibilityRawValue,
        id: { value: tournamentAccessibilityRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TournamentAccessibilityFormDefaults {
    return {
      id: null,
    };
  }
}
