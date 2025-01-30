import dayjs from 'dayjs/esm';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { ITeam } from 'app/entities/team/team.model';

export interface IMatch {
  id: number;
  matchIndex?: number | null;
  oneScore?: number | null;
  twoScore?: number | null;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  scoreSubmitted?: boolean | null;
  scoreApproved?: boolean | null;
  tournament?: Pick<ITournament, 'id'> | null;
  teamOne?: Pick<ITeam, 'id'> | null;
  teamTwo?: Pick<ITeam, 'id'> | null;
}

export type NewMatch = Omit<IMatch, 'id'> & { id: null };
