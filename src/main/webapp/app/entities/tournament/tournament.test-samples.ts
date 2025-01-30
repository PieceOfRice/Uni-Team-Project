import dayjs from 'dayjs/esm';

import { TournamentBracketType } from 'app/entities/enumerations/tournament-bracket-type.model';
import { AccessStatus } from 'app/entities/enumerations/access-status.model';
import { TournamentSetting } from 'app/entities/enumerations/tournament-setting.model';

import { ITournament, NewTournament } from './tournament.model';

export const sampleWithRequiredData: ITournament = {
  id: 73077,
  name: 'silver Fantastic Future-proofed',
  startTime: dayjs('2024-02-26T20:48'),
  accessStatus: AccessStatus['PRIVATE'],
  isLive: false,
  ended: false,
  gamesPerMatch: 17576,
  tournamentSetting: TournamentSetting['ONLINE'],
};

export const sampleWithPartialData: ITournament = {
  id: 6400,
  name: 'Generic Beauty',
  description: 'Sleek Accounts',
  prizePool: 52554,
  entryFee: 2782,
  startTime: dayjs('2024-02-26T19:39'),
  endTime: dayjs('2024-02-27T08:36'),
  bracketFormat: TournamentBracketType['SINGLE_ELIMINATION'],
  accessStatus: AccessStatus['PRIVATE'],
  isLive: true,
  ended: true,
  gamesPerMatch: 58637,
  maxParticipants: 8791,
  tournamentSetting: TournamentSetting['IN_PERSON'],
};

export const sampleWithFullData: ITournament = {
  id: 69669,
  name: 'Buckinghamshire',
  description: 'Internal Manager',
  prizePool: 46134,
  entryFee: 388,
  startTime: dayjs('2024-02-27T01:43'),
  endTime: dayjs('2024-02-27T14:35'),
  location: 'contingency Bike lavender',
  bracketFormat: TournamentBracketType['DOUBLE_ELIMINATION'],
  accessStatus: AccessStatus['PUBLIC'],
  isLive: false,
  ended: true,
  banner: '../fake-data/blob/hipster.png',
  bannerContentType: 'unknown',
  gamesPerMatch: 65297,
  maxParticipants: 33421,
  tournamentSetting: TournamentSetting['ONLINE'],
};

export const sampleWithNewData: NewTournament = {
  name: 'transmitting Table Branding',
  startTime: dayjs('2024-02-27T09:23'),
  accessStatus: AccessStatus['CLOSED'],
  isLive: false,
  ended: false,
  gamesPerMatch: 94305,
  tournamentSetting: TournamentSetting['IN_PERSON'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
