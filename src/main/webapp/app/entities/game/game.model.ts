import { IOverwatchMap } from 'app/entities/overwatch-map/overwatch-map.model';
import { IMatch } from 'app/entities/match/match.model';

export interface IGame {
  id: number;
  order?: number | null;
  scoreOne?: number | null;
  scoreTwo?: number | null;
  overwatchMap?: Pick<IOverwatchMap, 'id'> | null;
  match?: Pick<IMatch, 'id'> | null;
  replay?: string | null;
}

export type NewGame = Omit<IGame, 'id'> & { id: null };
