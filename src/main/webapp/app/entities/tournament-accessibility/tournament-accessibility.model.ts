import { ITournament } from 'app/entities/tournament/tournament.model';
import { VenueAccessibilities } from 'app/entities/enumerations/venue-accessibilities.model';

export interface ITournamentAccessibility {
  id: number;
  accessibility?: VenueAccessibilities | null;
  tournament?: Pick<ITournament, 'id'> | null;
}

export type NewTournamentAccessibility = Omit<ITournamentAccessibility, 'id'> & { id: null };
