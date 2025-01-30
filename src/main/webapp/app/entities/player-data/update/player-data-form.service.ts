import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPlayerData, NewPlayerData } from '../player-data.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPlayerData for edit and NewPlayerDataFormGroupInput for create.
 */
type PlayerDataFormGroupInput = IPlayerData | PartialWithRequiredKeyOf<NewPlayerData>;

type PlayerDataFormDefaults = Pick<NewPlayerData, 'id'>;

type PlayerDataFormGroupContent = {
  id: FormControl<IPlayerData['id'] | NewPlayerData['id']>;
  name: FormControl<IPlayerData['name']>;
  overwatchUsername: FormControl<IPlayerData['overwatchUsername']>;
  profile: FormControl<IPlayerData['profile']>;
  profileContentType: FormControl<IPlayerData['profileContentType']>;
  bio: FormControl<IPlayerData['bio']>;
  primaryLanguage: FormControl<IPlayerData['primaryLanguage']>;
  device: FormControl<IPlayerData['device']>;
  matchesPlayed: FormControl<IPlayerData['matchesPlayed']>;
  tournamentsPlayed: FormControl<IPlayerData['tournamentsPlayed']>;
  matchWins: FormControl<IPlayerData['matchWins']>;
  tournamentWins: FormControl<IPlayerData['tournamentWins']>;
  tournamentTop10s: FormControl<IPlayerData['tournamentTop10s']>;
  user: FormControl<IPlayerData['user']>;
};

export type PlayerDataFormGroup = FormGroup<PlayerDataFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PlayerDataFormService {
  createPlayerDataFormGroup(playerData: PlayerDataFormGroupInput = { id: null }): PlayerDataFormGroup {
    const playerDataRawValue = {
      ...this.getFormDefaults(),
      ...playerData,
    };
    return new FormGroup<PlayerDataFormGroupContent>({
      id: new FormControl(
        { value: playerDataRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(playerDataRawValue.name, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(50)],
      }),
      overwatchUsername: new FormControl(playerDataRawValue.overwatchUsername, {
        validators: [Validators.minLength(3), Validators.maxLength(40)],
      }),
      profile: new FormControl(playerDataRawValue.profile),
      profileContentType: new FormControl(playerDataRawValue.profileContentType),
      bio: new FormControl(playerDataRawValue.bio, {
        validators: [Validators.maxLength(1000)],
      }),
      primaryLanguage: new FormControl(playerDataRawValue.primaryLanguage),
      device: new FormControl(playerDataRawValue.device),
      matchesPlayed: new FormControl(playerDataRawValue.matchesPlayed, {
        validators: [Validators.required, Validators.min(0)],
      }),
      tournamentsPlayed: new FormControl(playerDataRawValue.tournamentsPlayed, {
        validators: [Validators.required, Validators.min(0)],
      }),
      matchWins: new FormControl(playerDataRawValue.matchWins, {
        validators: [Validators.required, Validators.min(0)],
      }),
      tournamentWins: new FormControl(playerDataRawValue.tournamentWins, {
        validators: [Validators.required, Validators.min(0)],
      }),
      tournamentTop10s: new FormControl(playerDataRawValue.tournamentTop10s, {
        validators: [Validators.min(0)],
      }),
      user: new FormControl(playerDataRawValue.user),
    });
  }

  getPlayerData(form: PlayerDataFormGroup): IPlayerData | NewPlayerData {
    return form.getRawValue() as IPlayerData | NewPlayerData;
  }

  resetForm(form: PlayerDataFormGroup, playerData: PlayerDataFormGroupInput): void {
    const playerDataRawValue = { ...this.getFormDefaults(), ...playerData };
    form.reset(
      {
        ...playerDataRawValue,
        id: { value: playerDataRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PlayerDataFormDefaults {
    return {
      id: null,
    };
  }
}
