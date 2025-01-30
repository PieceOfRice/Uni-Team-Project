import { ITeam } from 'app/entities/team/team.model';
import { ITournament } from 'app/entities/tournament/tournament.model';

export interface IParticipant {
  id: number;
  signUpRank?: number | null;
  team?: Pick<ITeam, 'id'> | null;
  tournament?: Pick<ITournament, 'id'> | null;
}

export type NewParticipant = Omit<IParticipant, 'id'> & { id: null };
