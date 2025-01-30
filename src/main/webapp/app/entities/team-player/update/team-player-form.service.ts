import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITeamPlayer, NewTeamPlayer } from '../team-player.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeamPlayer for edit and NewTeamPlayerFormGroupInput for create.
 */
type TeamPlayerFormGroupInput = ITeamPlayer | PartialWithRequiredKeyOf<NewTeamPlayer>;

type TeamPlayerFormDefaults = Pick<NewTeamPlayer, 'id'>;

type TeamPlayerFormGroupContent = {
  id: FormControl<ITeamPlayer['id'] | NewTeamPlayer['id']>;
  role: FormControl<ITeamPlayer['role']>;
  player: FormControl<ITeamPlayer['player']>;
  team: FormControl<ITeamPlayer['team']>;
};

export type TeamPlayerFormGroup = FormGroup<TeamPlayerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeamPlayerFormService {
  createTeamPlayerFormGroup(teamPlayer: TeamPlayerFormGroupInput = { id: null }): TeamPlayerFormGroup {
    const teamPlayerRawValue = {
      ...this.getFormDefaults(),
      ...teamPlayer,
    };
    return new FormGroup<TeamPlayerFormGroupContent>({
      id: new FormControl(
        { value: teamPlayerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      role: new FormControl(teamPlayerRawValue.role, {
        validators: [Validators.required],
      }),
      player: new FormControl(teamPlayerRawValue.player),
      team: new FormControl(teamPlayerRawValue.team),
    });
  }

  getTeamPlayer(form: TeamPlayerFormGroup): ITeamPlayer | NewTeamPlayer {
    return form.getRawValue() as ITeamPlayer | NewTeamPlayer;
  }

  resetForm(form: TeamPlayerFormGroup, teamPlayer: TeamPlayerFormGroupInput): void {
    const teamPlayerRawValue = { ...this.getFormDefaults(), ...teamPlayer };
    form.reset(
      {
        ...teamPlayerRawValue,
        id: { value: teamPlayerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TeamPlayerFormDefaults {
    return {
      id: null,
    };
  }
}
