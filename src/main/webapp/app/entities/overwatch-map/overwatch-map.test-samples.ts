import { MapMode } from 'app/entities/enumerations/map-mode.model';

import { IOverwatchMap, NewOverwatchMap } from './overwatch-map.model';

export const sampleWithRequiredData: IOverwatchMap = {
  id: 11983,
  name: 'blue RSS e-markets',
  mode: MapMode['PUSH'],
};

export const sampleWithPartialData: IOverwatchMap = {
  id: 53251,
  name: 'National Directives Myanmar',
  mode: MapMode['CONTROL'],
};

export const sampleWithFullData: IOverwatchMap = {
  id: 50608,
  name: 'Burkina Planner leading',
  mode: MapMode['HYBRID'],
};

export const sampleWithNewData: NewOverwatchMap = {
  name: 'withdrawal Multi-layered',
  mode: MapMode['ESCORT'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
