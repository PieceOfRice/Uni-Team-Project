import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITournamentAccessibility } from '../tournament-accessibility.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../tournament-accessibility.test-samples';

import { TournamentAccessibilityService } from './tournament-accessibility.service';

const requireRestSample: ITournamentAccessibility = {
  ...sampleWithRequiredData,
};

describe('TournamentAccessibility Service', () => {
  let service: TournamentAccessibilityService;
  let httpMock: HttpTestingController;
  let expectedResult: ITournamentAccessibility | ITournamentAccessibility[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TournamentAccessibilityService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a TournamentAccessibility', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const tournamentAccessibility = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tournamentAccessibility).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TournamentAccessibility', () => {
      const tournamentAccessibility = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tournamentAccessibility).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TournamentAccessibility', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TournamentAccessibility', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TournamentAccessibility', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTournamentAccessibilityToCollectionIfMissing', () => {
      it('should add a TournamentAccessibility to an empty array', () => {
        const tournamentAccessibility: ITournamentAccessibility = sampleWithRequiredData;
        expectedResult = service.addTournamentAccessibilityToCollectionIfMissing([], tournamentAccessibility);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tournamentAccessibility);
      });

      it('should not add a TournamentAccessibility to an array that contains it', () => {
        const tournamentAccessibility: ITournamentAccessibility = sampleWithRequiredData;
        const tournamentAccessibilityCollection: ITournamentAccessibility[] = [
          {
            ...tournamentAccessibility,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTournamentAccessibilityToCollectionIfMissing(
          tournamentAccessibilityCollection,
          tournamentAccessibility
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TournamentAccessibility to an array that doesn't contain it", () => {
        const tournamentAccessibility: ITournamentAccessibility = sampleWithRequiredData;
        const tournamentAccessibilityCollection: ITournamentAccessibility[] = [sampleWithPartialData];
        expectedResult = service.addTournamentAccessibilityToCollectionIfMissing(
          tournamentAccessibilityCollection,
          tournamentAccessibility
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tournamentAccessibility);
      });

      it('should add only unique TournamentAccessibility to an array', () => {
        const tournamentAccessibilityArray: ITournamentAccessibility[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const tournamentAccessibilityCollection: ITournamentAccessibility[] = [sampleWithRequiredData];
        expectedResult = service.addTournamentAccessibilityToCollectionIfMissing(
          tournamentAccessibilityCollection,
          ...tournamentAccessibilityArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tournamentAccessibility: ITournamentAccessibility = sampleWithRequiredData;
        const tournamentAccessibility2: ITournamentAccessibility = sampleWithPartialData;
        expectedResult = service.addTournamentAccessibilityToCollectionIfMissing([], tournamentAccessibility, tournamentAccessibility2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tournamentAccessibility);
        expect(expectedResult).toContain(tournamentAccessibility2);
      });

      it('should accept null and undefined values', () => {
        const tournamentAccessibility: ITournamentAccessibility = sampleWithRequiredData;
        expectedResult = service.addTournamentAccessibilityToCollectionIfMissing([], null, tournamentAccessibility, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tournamentAccessibility);
      });

      it('should return initial array if no TournamentAccessibility is added', () => {
        const tournamentAccessibilityCollection: ITournamentAccessibility[] = [sampleWithRequiredData];
        expectedResult = service.addTournamentAccessibilityToCollectionIfMissing(tournamentAccessibilityCollection, undefined, null);
        expect(expectedResult).toEqual(tournamentAccessibilityCollection);
      });
    });

    describe('compareTournamentAccessibility', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTournamentAccessibility(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTournamentAccessibility(entity1, entity2);
        const compareResult2 = service.compareTournamentAccessibility(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTournamentAccessibility(entity1, entity2);
        const compareResult2 = service.compareTournamentAccessibility(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTournamentAccessibility(entity1, entity2);
        const compareResult2 = service.compareTournamentAccessibility(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
