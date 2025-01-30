import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOverwatchMap } from '../overwatch-map.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../overwatch-map.test-samples';

import { OverwatchMapService } from './overwatch-map.service';

const requireRestSample: IOverwatchMap = {
  ...sampleWithRequiredData,
};

describe('OverwatchMap Service', () => {
  let service: OverwatchMapService;
  let httpMock: HttpTestingController;
  let expectedResult: IOverwatchMap | IOverwatchMap[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OverwatchMapService);
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

    it('should create a OverwatchMap', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const overwatchMap = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(overwatchMap).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OverwatchMap', () => {
      const overwatchMap = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(overwatchMap).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OverwatchMap', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OverwatchMap', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OverwatchMap', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOverwatchMapToCollectionIfMissing', () => {
      it('should add a OverwatchMap to an empty array', () => {
        const overwatchMap: IOverwatchMap = sampleWithRequiredData;
        expectedResult = service.addOverwatchMapToCollectionIfMissing([], overwatchMap);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(overwatchMap);
      });

      it('should not add a OverwatchMap to an array that contains it', () => {
        const overwatchMap: IOverwatchMap = sampleWithRequiredData;
        const overwatchMapCollection: IOverwatchMap[] = [
          {
            ...overwatchMap,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOverwatchMapToCollectionIfMissing(overwatchMapCollection, overwatchMap);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OverwatchMap to an array that doesn't contain it", () => {
        const overwatchMap: IOverwatchMap = sampleWithRequiredData;
        const overwatchMapCollection: IOverwatchMap[] = [sampleWithPartialData];
        expectedResult = service.addOverwatchMapToCollectionIfMissing(overwatchMapCollection, overwatchMap);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(overwatchMap);
      });

      it('should add only unique OverwatchMap to an array', () => {
        const overwatchMapArray: IOverwatchMap[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const overwatchMapCollection: IOverwatchMap[] = [sampleWithRequiredData];
        expectedResult = service.addOverwatchMapToCollectionIfMissing(overwatchMapCollection, ...overwatchMapArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const overwatchMap: IOverwatchMap = sampleWithRequiredData;
        const overwatchMap2: IOverwatchMap = sampleWithPartialData;
        expectedResult = service.addOverwatchMapToCollectionIfMissing([], overwatchMap, overwatchMap2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(overwatchMap);
        expect(expectedResult).toContain(overwatchMap2);
      });

      it('should accept null and undefined values', () => {
        const overwatchMap: IOverwatchMap = sampleWithRequiredData;
        expectedResult = service.addOverwatchMapToCollectionIfMissing([], null, overwatchMap, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(overwatchMap);
      });

      it('should return initial array if no OverwatchMap is added', () => {
        const overwatchMapCollection: IOverwatchMap[] = [sampleWithRequiredData];
        expectedResult = service.addOverwatchMapToCollectionIfMissing(overwatchMapCollection, undefined, null);
        expect(expectedResult).toEqual(overwatchMapCollection);
      });
    });

    describe('compareOverwatchMap', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOverwatchMap(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareOverwatchMap(entity1, entity2);
        const compareResult2 = service.compareOverwatchMap(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareOverwatchMap(entity1, entity2);
        const compareResult2 = service.compareOverwatchMap(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareOverwatchMap(entity1, entity2);
        const compareResult2 = service.compareOverwatchMap(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
