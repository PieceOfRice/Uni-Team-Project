import dayjs from 'dayjs/esm';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { TournamentBracketType } from 'app/entities/enumerations/tournament-bracket-type.model';
import { AccessStatus } from 'app/entities/enumerations/access-status.model';
import { TournamentSetting } from 'app/entities/enumerations/tournament-setting.model';
import { ITeam } from 'app/entities/team/team.model';
import { IMatch } from 'app/entities/match/match.model';

export interface ITournament {
  id: number;
  name?: string | null;
  description?: string | null;
  prizePool?: number | null;
  entryFee?: number | null;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  location?: string | null;
  bracketFormat?: TournamentBracketType | null;
  accessStatus?: AccessStatus | null;
  isLive?: boolean | null;
  ended?: boolean | null;
  banner?: string | null;
  bannerContentType?: string | null;
  gamesPerMatch?: number | null;
  maxParticipants?: number | null;
  tournamentSetting?: TournamentSetting | null;
  creator?: Pick<IPlayerData, 'id'> | null;
}

export type NewTournament = Omit<ITournament, 'id'> & { id: null };
