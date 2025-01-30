import { ITeam, NewTeam } from './team.model';

export const sampleWithRequiredData: ITeam = {
  id: 88024,
  name: 'Chief programming',
};

export const sampleWithPartialData: ITeam = {
  id: 3443,
  name: 'lavender application Automotive',
  logo: '../fake-data/blob/hipster.png',
  logoContentType: 'unknown',
  banner: '../fake-data/blob/hipster.png',
  bannerContentType: 'unknown',
  description: 'Portugal deposit',
};

export const sampleWithFullData: ITeam = {
  id: 97879,
  name: 'Grocery Berkshire',
  logo: '../fake-data/blob/hipster.png',
  logoContentType: 'unknown',
  banner: '../fake-data/blob/hipster.png',
  bannerContentType: 'unknown',
  description: 'Maine experiences',
};

export const sampleWithNewData: NewTeam = {
  name: 'Rhode Facilitator Afghanistan',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
