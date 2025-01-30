import { IGame } from 'app/entities/game/game.model';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { GameTeam } from 'app/entities/enumerations/game-team.model';

export interface IGamePlayer {
  id: number;
  team?: GameTeam | null;
  game?: Pick<IGame, 'id'> | null;
  playerData?: Pick<IPlayerData, 'id'> | null;
}

export type NewGamePlayer = Omit<IGamePlayer, 'id'> & { id: null };
