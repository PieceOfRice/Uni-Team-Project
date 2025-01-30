import { IParticipant, NewParticipant } from './participant.model';

export const sampleWithRequiredData: IParticipant = {
  id: 35428,
  signUpRank: 55210,
};

export const sampleWithPartialData: IParticipant = {
  id: 6288,
  signUpRank: 31810,
};

export const sampleWithFullData: IParticipant = {
  id: 31073,
  signUpRank: 84793,
};

export const sampleWithNewData: NewParticipant = {
  signUpRank: 36112,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
