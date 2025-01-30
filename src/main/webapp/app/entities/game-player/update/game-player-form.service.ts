import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IGamePlayer, NewGamePlayer } from '../game-player.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGamePlayer for edit and NewGamePlayerFormGroupInput for create.
 */
type GamePlayerFormGroupInput = IGamePlayer | PartialWithRequiredKeyOf<NewGamePlayer>;

type GamePlayerFormDefaults = Pick<NewGamePlayer, 'id'>;

type GamePlayerFormGroupContent = {
  id: FormControl<IGamePlayer['id'] | NewGamePlayer['id']>;
  team: FormControl<IGamePlayer['team']>;
  game: FormControl<IGamePlayer['game']>;
  playerData: FormControl<IGamePlayer['playerData']>;
};

export type GamePlayerFormGroup = FormGroup<GamePlayerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GamePlayerFormService {
  createGamePlayerFormGroup(gamePlayer: GamePlayerFormGroupInput = { id: null }): GamePlayerFormGroup {
    const gamePlayerRawValue = {
      ...this.getFormDefaults(),
      ...gamePlayer,
    };
    return new FormGroup<GamePlayerFormGroupContent>({
      id: new FormControl(
        { value: gamePlayerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      team: new FormControl(gamePlayerRawValue.team, {
        validators: [Validators.required],
      }),
      game: new FormControl(gamePlayerRawValue.game),
      playerData: new FormControl(gamePlayerRawValue.playerData),
    });
  }

  getGamePlayer(form: GamePlayerFormGroup): IGamePlayer | NewGamePlayer {
    return form.getRawValue() as IGamePlayer | NewGamePlayer;
  }

  resetForm(form: GamePlayerFormGroup, gamePlayer: GamePlayerFormGroupInput): void {
    const gamePlayerRawValue = { ...this.getFormDefaults(), ...gamePlayer };
    form.reset(
      {
        ...gamePlayerRawValue,
        id: { value: gamePlayerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): GamePlayerFormDefaults {
    return {
      id: null,
    };
  }
}
