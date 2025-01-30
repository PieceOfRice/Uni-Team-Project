import { GameTeam } from 'app/entities/enumerations/game-team.model';

import { IGamePlayer, NewGamePlayer } from './game-player.model';

export const sampleWithRequiredData: IGamePlayer = {
  id: 8447,
  team: GameTeam['TEAM_TWO'],
};

export const sampleWithPartialData: IGamePlayer = {
  id: 21546,
  team: GameTeam['TEAM_ONE'],
};

export const sampleWithFullData: IGamePlayer = {
  id: 23161,
  team: GameTeam['TEAM_TWO'],
};

export const sampleWithNewData: NewGamePlayer = {
  team: GameTeam['TEAM_TWO'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
