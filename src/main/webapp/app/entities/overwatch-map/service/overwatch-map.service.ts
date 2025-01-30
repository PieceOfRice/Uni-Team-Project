import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOverwatchMap, NewOverwatchMap } from '../overwatch-map.model';

export type PartialUpdateOverwatchMap = Partial<IOverwatchMap> & Pick<IOverwatchMap, 'id'>;

export type EntityResponseType = HttpResponse<IOverwatchMap>;
export type EntityArrayResponseType = HttpResponse<IOverwatchMap[]>;

@Injectable({ providedIn: 'root' })
export class OverwatchMapService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/overwatch-maps');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(overwatchMap: NewOverwatchMap): Observable<EntityResponseType> {
    return this.http.post<IOverwatchMap>(this.resourceUrl, overwatchMap, { observe: 'response' });
  }

  update(overwatchMap: IOverwatchMap): Observable<EntityResponseType> {
    return this.http.put<IOverwatchMap>(`${this.resourceUrl}/${this.getOverwatchMapIdentifier(overwatchMap)}`, overwatchMap, {
      observe: 'response',
    });
  }

  partialUpdate(overwatchMap: PartialUpdateOverwatchMap): Observable<EntityResponseType> {
    return this.http.patch<IOverwatchMap>(`${this.resourceUrl}/${this.getOverwatchMapIdentifier(overwatchMap)}`, overwatchMap, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOverwatchMap>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOverwatchMap[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOverwatchMapIdentifier(overwatchMap: Pick<IOverwatchMap, 'id'>): number {
    return overwatchMap.id;
  }

  compareOverwatchMap(o1: Pick<IOverwatchMap, 'id'> | null, o2: Pick<IOverwatchMap, 'id'> | null): boolean {
    return o1 && o2 ? this.getOverwatchMapIdentifier(o1) === this.getOverwatchMapIdentifier(o2) : o1 === o2;
  }

  addOverwatchMapToCollectionIfMissing<Type extends Pick<IOverwatchMap, 'id'>>(
    overwatchMapCollection: Type[],
    ...overwatchMapsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const overwatchMaps: Type[] = overwatchMapsToCheck.filter(isPresent);
    if (overwatchMaps.length > 0) {
      const overwatchMapCollectionIdentifiers = overwatchMapCollection.map(
        overwatchMapItem => this.getOverwatchMapIdentifier(overwatchMapItem)!
      );
      const overwatchMapsToAdd = overwatchMaps.filter(overwatchMapItem => {
        const overwatchMapIdentifier = this.getOverwatchMapIdentifier(overwatchMapItem);
        if (overwatchMapCollectionIdentifiers.includes(overwatchMapIdentifier)) {
          return false;
        }
        overwatchMapCollectionIdentifiers.push(overwatchMapIdentifier);
        return true;
      });
      return [...overwatchMapsToAdd, ...overwatchMapCollection];
    }
    return overwatchMapCollection;
  }
}
