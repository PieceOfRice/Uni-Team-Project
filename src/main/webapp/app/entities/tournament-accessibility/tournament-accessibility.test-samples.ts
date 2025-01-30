import { VenueAccessibilities } from 'app/entities/enumerations/venue-accessibilities.model';

import { ITournamentAccessibility, NewTournamentAccessibility } from './tournament-accessibility.model';

export const sampleWithRequiredData: ITournamentAccessibility = {
  id: 76773,
  accessibility: VenueAccessibilities['ACCESSIBLE_PARKING'],
};

export const sampleWithPartialData: ITournamentAccessibility = {
  id: 92334,
  accessibility: VenueAccessibilities['RAMPS'],
};

export const sampleWithFullData: ITournamentAccessibility = {
  id: 36395,
  accessibility: VenueAccessibilities['ACCESSIBLE_PARKING'],
};

export const sampleWithNewData: NewTournamentAccessibility = {
  accessibility: VenueAccessibilities['ACCESSIBLE_PARKING'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
