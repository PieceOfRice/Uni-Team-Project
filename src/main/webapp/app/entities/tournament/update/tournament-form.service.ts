import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITournament, NewTournament } from '../tournament.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITournament for edit and NewTournamentFormGroupInput for create.
 */
type TournamentFormGroupInput = ITournament | PartialWithRequiredKeyOf<NewTournament>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITournament | NewTournament> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

type TournamentFormRawValue = FormValueOf<ITournament>;

type NewTournamentFormRawValue = FormValueOf<NewTournament>;

type TournamentFormDefaults = Pick<NewTournament, 'id' | 'startTime' | 'endTime' | 'isLive' | 'ended'>;

type TournamentFormGroupContent = {
  id: FormControl<TournamentFormRawValue['id'] | NewTournament['id']>;
  name: FormControl<TournamentFormRawValue['name']>;
  description: FormControl<TournamentFormRawValue['description']>;
  prizePool: FormControl<TournamentFormRawValue['prizePool']>;
  entryFee: FormControl<TournamentFormRawValue['entryFee']>;
  startTime: FormControl<TournamentFormRawValue['startTime']>;
  endTime: FormControl<TournamentFormRawValue['endTime']>;
  location: FormControl<TournamentFormRawValue['location']>;
  bracketFormat: FormControl<TournamentFormRawValue['bracketFormat']>;
  accessStatus: FormControl<TournamentFormRawValue['accessStatus']>;
  isLive: FormControl<TournamentFormRawValue['isLive']>;
  ended: FormControl<TournamentFormRawValue['ended']>;
  banner: FormControl<TournamentFormRawValue['banner']>;
  bannerContentType: FormControl<TournamentFormRawValue['bannerContentType']>;
  gamesPerMatch: FormControl<TournamentFormRawValue['gamesPerMatch']>;
  maxParticipants: FormControl<TournamentFormRawValue['maxParticipants']>;
  tournamentSetting: FormControl<TournamentFormRawValue['tournamentSetting']>;
  creator: FormControl<TournamentFormRawValue['creator']>;
};

export type TournamentFormGroup = FormGroup<TournamentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TournamentFormService {
  createTournamentFormGroup(tournament: TournamentFormGroupInput = { id: null }): TournamentFormGroup {
    const tournamentRawValue = this.convertTournamentToTournamentRawValue({
      ...this.getFormDefaults(),
      ...tournament,
    });
    return new FormGroup<TournamentFormGroupContent>({
      id: new FormControl(
        { value: tournamentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(tournamentRawValue.name, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(40)],
      }),
      description: new FormControl(tournamentRawValue.description, {
        validators: [Validators.maxLength(5000)],
      }),
      prizePool: new FormControl(tournamentRawValue.prizePool),
      entryFee: new FormControl(tournamentRawValue.entryFee),
      startTime: new FormControl(tournamentRawValue.startTime, {
        validators: [Validators.required],
      }),
      endTime: new FormControl(tournamentRawValue.endTime),
      location: new FormControl(tournamentRawValue.location),
      bracketFormat: new FormControl(tournamentRawValue.bracketFormat),
      accessStatus: new FormControl(tournamentRawValue.accessStatus, {
        validators: [Validators.required],
      }),
      isLive: new FormControl(tournamentRawValue.isLive, {
        validators: [Validators.required],
      }),
      ended: new FormControl(tournamentRawValue.ended, {
        validators: [Validators.required],
      }),
      banner: new FormControl(tournamentRawValue.banner),
      bannerContentType: new FormControl(tournamentRawValue.bannerContentType),
      gamesPerMatch: new FormControl(tournamentRawValue.gamesPerMatch, {
        validators: [Validators.required],
      }),
      maxParticipants: new FormControl(tournamentRawValue.maxParticipants),
      tournamentSetting: new FormControl(tournamentRawValue.tournamentSetting, {
        validators: [Validators.required],
      }),
      creator: new FormControl(tournamentRawValue.creator),
    });
  }

  getTournament(form: TournamentFormGroup): ITournament | NewTournament {
    return this.convertTournamentRawValueToTournament(form.getRawValue() as TournamentFormRawValue | NewTournamentFormRawValue);
  }

  resetForm(form: TournamentFormGroup, tournament: TournamentFormGroupInput): void {
    const tournamentRawValue = this.convertTournamentToTournamentRawValue({ ...this.getFormDefaults(), ...tournament });
    form.reset(
      {
        ...tournamentRawValue,
        id: { value: tournamentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TournamentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startTime: currentTime,
      endTime: currentTime,
      isLive: false,
      ended: false,
    };
  }

  private convertTournamentRawValueToTournament(
    rawTournament: TournamentFormRawValue | NewTournamentFormRawValue
  ): ITournament | NewTournament {
    return {
      ...rawTournament,
      startTime: dayjs(rawTournament.startTime, DATE_TIME_FORMAT),
      endTime: dayjs(rawTournament.endTime, DATE_TIME_FORMAT),
    };
  }

  private convertTournamentToTournamentRawValue(
    tournament: ITournament | (Partial<NewTournament> & TournamentFormDefaults)
  ): TournamentFormRawValue | PartialWithRequiredKeyOf<NewTournamentFormRawValue> {
    return {
      ...tournament,
      startTime: tournament.startTime ? tournament.startTime.format(DATE_TIME_FORMAT) : undefined,
      endTime: tournament.endTime ? tournament.endTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
