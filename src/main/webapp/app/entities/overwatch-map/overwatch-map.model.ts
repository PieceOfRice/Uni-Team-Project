import { MapMode } from 'app/entities/enumerations/map-mode.model';

export interface IOverwatchMap {
  id: number;
  name?: string | null;
  mode?: MapMode | null;
}

export type NewOverwatchMap = Omit<IOverwatchMap, 'id'> & { id: null };
