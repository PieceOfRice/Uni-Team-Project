import { AccessStatus } from '../enumerations/access-status.model';

export interface ITeam {
  id: number;
  name?: string | null;
  logo?: string | null;
  logoContentType?: string | null;
  banner?: string | null;
  bannerContentType?: string | null;
  description?: string | null;
  accessStatus?: AccessStatus | null;
}

export type NewTeam = Omit<ITeam, 'id'> & { id: null };
