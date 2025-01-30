import { PlayerLanguage } from 'app/entities/enumerations/player-language.model';
import { PlayerDevice } from 'app/entities/enumerations/player-device.model';

import { IPlayerData, NewPlayerData } from './player-data.model';

export const sampleWithRequiredData: IPlayerData = {
  id: 28757,
  name: 'Rustic',
  matchesPlayed: 16467,
  tournamentsPlayed: 1720,
  matchWins: 80639,
  tournamentWins: 46743,
};

export const sampleWithPartialData: IPlayerData = {
  id: 54626,
  name: 'Customer invoice',
  primaryLanguage: PlayerLanguage['OTHER'],
  device: PlayerDevice['DESKTOP'],
  matchesPlayed: 87048,
  tournamentsPlayed: 95002,
  matchWins: 84149,
  tournamentWins: 31322,
};

export const sampleWithFullData: IPlayerData = {
  id: 79971,
  name: 'Investor Unbranded',
  overwatchUsername: 'Fish Account',
  profile: '../fake-data/blob/hipster.png',
  profileContentType: 'unknown',
  bio: 'Incredible Functionality',
  primaryLanguage: PlayerLanguage['OTHER'],
  device: PlayerDevice['DESKTOP'],
  matchesPlayed: 92123,
  tournamentsPlayed: 10998,
  matchWins: 65106,
  tournamentWins: 23141,
  tournamentTop10s: 70428,
};

export const sampleWithNewData: NewPlayerData = {
  name: 'Pants',
  matchesPlayed: 64212,
  tournamentsPlayed: 52827,
  matchWins: 59489,
  tournamentWins: 69182,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
