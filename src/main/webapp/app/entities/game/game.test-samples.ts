import { IGame, NewGame } from './game.model';

export const sampleWithRequiredData: IGame = {
  id: 79326,
  order: 59043,
  scoreOne: 12042,
  scoreTwo: 64112,
};

export const sampleWithPartialData: IGame = {
  id: 23780,
  order: 64736,
  scoreOne: 33113,
  scoreTwo: 82003,
};

export const sampleWithFullData: IGame = {
  id: 42653,
  order: 92409,
  scoreOne: 53329,
  scoreTwo: 55053,
};

export const sampleWithNewData: NewGame = {
  order: 78946,
  scoreOne: 48664,
  scoreTwo: 33227,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
