import dayjs from 'dayjs/esm';

import { IMatch, NewMatch } from './match.model';

export const sampleWithRequiredData: IMatch = {
  id: 82797,
  matchIndex: 75968,
  scoreSubmitted: false,
  scoreApproved: true,
};

export const sampleWithPartialData: IMatch = {
  id: 35227,
  matchIndex: 56722,
  oneScore: 29988,
  startTime: dayjs('2024-02-27T07:15'),
  endTime: dayjs('2024-02-27T06:48'),
  scoreSubmitted: true,
  scoreApproved: false,
};

export const sampleWithFullData: IMatch = {
  id: 30757,
  matchIndex: 24405,
  oneScore: 95492,
  twoScore: 61589,
  startTime: dayjs('2024-02-27T06:37'),
  endTime: dayjs('2024-02-26T21:57'),
  scoreSubmitted: false,
  scoreApproved: false,
};

export const sampleWithNewData: NewMatch = {
  matchIndex: 14043,
  scoreSubmitted: true,
  scoreApproved: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
