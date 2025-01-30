import { IUser } from 'app/entities/user/user.model';
import { PlayerLanguage } from 'app/entities/enumerations/player-language.model';
import { PlayerDevice } from 'app/entities/enumerations/player-device.model';

export interface IPlayerData {
  id: number;
  name?: string | null;
  overwatchUsername?: string | null;
  profile?: string | null;
  profileContentType?: string | null;
  bio?: string | null;
  primaryLanguage?: PlayerLanguage | null;
  device?: PlayerDevice | null;
  matchesPlayed?: number | null;
  tournamentsPlayed?: number | null;
  matchWins?: number | null;
  tournamentWins?: number | null;
  tournamentTop10s?: number | null;
  user?: Pick<IUser, 'id'> | null;
  base64Profile?: string | null;
  teamRole?: string | null;
}

export type NewPlayerData = Omit<IPlayerData, 'id'> & { id: null };
