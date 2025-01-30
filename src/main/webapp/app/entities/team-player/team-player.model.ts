import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { ITeam } from 'app/entities/team/team.model';
import { TeamRole } from 'app/entities/enumerations/team-role.model';

export interface ITeamPlayer {
  id: number;
  role?: TeamRole | null;
  player?: IPlayerData | null;
  team?: Pick<ITeam, 'id'> | null;
  accepted: boolean;
}

export type NewTeamPlayer = Omit<ITeamPlayer, 'id'> & { id: null };
