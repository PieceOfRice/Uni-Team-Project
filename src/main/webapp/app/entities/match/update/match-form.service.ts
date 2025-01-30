import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMatch, NewMatch } from '../match.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMatch for edit and NewMatchFormGroupInput for create.
 */
type MatchFormGroupInput = IMatch | PartialWithRequiredKeyOf<NewMatch>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMatch | NewMatch> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

type MatchFormRawValue = FormValueOf<IMatch>;

type NewMatchFormRawValue = FormValueOf<NewMatch>;

type MatchFormDefaults = Pick<NewMatch, 'id' | 'startTime' | 'endTime' | 'scoreSubmitted' | 'scoreApproved'>;

type MatchFormGroupContent = {
  id: FormControl<MatchFormRawValue['id'] | NewMatch['id']>;
  matchIndex: FormControl<MatchFormRawValue['matchIndex']>;
  oneScore: FormControl<MatchFormRawValue['oneScore']>;
  twoScore: FormControl<MatchFormRawValue['twoScore']>;
  startTime: FormControl<MatchFormRawValue['startTime']>;
  endTime: FormControl<MatchFormRawValue['endTime']>;
  scoreSubmitted: FormControl<MatchFormRawValue['scoreSubmitted']>;
  scoreApproved: FormControl<MatchFormRawValue['scoreApproved']>;
  tournament: FormControl<MatchFormRawValue['tournament']>;
  teamOne: FormControl<MatchFormRawValue['teamOne']>;
  teamTwo: FormControl<MatchFormRawValue['teamTwo']>;
};

export type MatchFormGroup = FormGroup<MatchFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MatchFormService {
  createMatchFormGroup(match: MatchFormGroupInput = { id: null }): MatchFormGroup {
    const matchRawValue = this.convertMatchToMatchRawValue({
      ...this.getFormDefaults(),
      ...match,
    });
    return new FormGroup<MatchFormGroupContent>({
      id: new FormControl(
        { value: matchRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      matchIndex: new FormControl(matchRawValue.matchIndex, {
        validators: [Validators.required],
      }),
      oneScore: new FormControl(matchRawValue.oneScore, {
        validators: [Validators.min(0)],
      }),
      twoScore: new FormControl(matchRawValue.twoScore, {
        validators: [Validators.min(0)],
      }),
      startTime: new FormControl(matchRawValue.startTime),
      endTime: new FormControl(matchRawValue.endTime),
      scoreSubmitted: new FormControl(matchRawValue.scoreSubmitted, {
        validators: [Validators.required],
      }),
      scoreApproved: new FormControl(matchRawValue.scoreApproved, {
        validators: [Validators.required],
      }),
      tournament: new FormControl(matchRawValue.tournament),
      teamOne: new FormControl(matchRawValue.teamOne),
      teamTwo: new FormControl(matchRawValue.teamTwo),
    });
  }

  getMatch(form: MatchFormGroup): IMatch | NewMatch {
    return this.convertMatchRawValueToMatch(form.getRawValue() as MatchFormRawValue | NewMatchFormRawValue);
  }

  resetForm(form: MatchFormGroup, match: MatchFormGroupInput): void {
    const matchRawValue = this.convertMatchToMatchRawValue({ ...this.getFormDefaults(), ...match });
    form.reset(
      {
        ...matchRawValue,
        id: { value: matchRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): MatchFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startTime: currentTime,
      endTime: currentTime,
      scoreSubmitted: false,
      scoreApproved: false,
    };
  }

  private convertMatchRawValueToMatch(rawMatch: MatchFormRawValue | NewMatchFormRawValue): IMatch | NewMatch {
    return {
      ...rawMatch,
      startTime: dayjs(rawMatch.startTime, DATE_TIME_FORMAT),
      endTime: dayjs(rawMatch.endTime, DATE_TIME_FORMAT),
    };
  }

  private convertMatchToMatchRawValue(
    match: IMatch | (Partial<NewMatch> & MatchFormDefaults)
  ): MatchFormRawValue | PartialWithRequiredKeyOf<NewMatchFormRawValue> {
    return {
      ...match,
      startTime: match.startTime ? match.startTime.format(DATE_TIME_FORMAT) : undefined,
      endTime: match.endTime ? match.endTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
