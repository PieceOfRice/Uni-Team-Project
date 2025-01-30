import { TeamRole } from 'app/entities/enumerations/team-role.model';

import { ITeamPlayer, NewTeamPlayer } from './team-player.model';

export const sampleWithRequiredData: ITeamPlayer = {
  id: 64111,
  role: TeamRole['LEADER'],
};

export const sampleWithPartialData: ITeamPlayer = {
  id: 89285,
  role: TeamRole['LEADER'],
};

export const sampleWithFullData: ITeamPlayer = {
  id: 55503,
  role: TeamRole['PLAYER'],
};

export const sampleWithNewData: NewTeamPlayer = {
  role: TeamRole['LEADER'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
