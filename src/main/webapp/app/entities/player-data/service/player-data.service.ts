import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlayerData, NewPlayerData } from '../player-data.model';

export type PartialUpdatePlayerData = Partial<IPlayerData> & Pick<IPlayerData, 'id'>;

export type EntityResponseType = HttpResponse<IPlayerData>;
export type EntityArrayResponseType = HttpResponse<IPlayerData[]>;

@Injectable({ providedIn: 'root' })
export class PlayerDataService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/player-data');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(playerData: NewPlayerData): Observable<EntityResponseType> {
    return this.http.post<IPlayerData>(this.resourceUrl, playerData, { observe: 'response' });
  }

  update(playerData: IPlayerData): Observable<EntityResponseType> {
    return this.http.put<IPlayerData>(`${this.resourceUrl}/${this.getPlayerDataIdentifier(playerData)}`, playerData, {
      observe: 'response',
    });
  }

  partialUpdate(playerData: PartialUpdatePlayerData): Observable<EntityResponseType> {
    return this.http.patch<IPlayerData>(`${this.resourceUrl}/${this.getPlayerDataIdentifier(playerData)}`, playerData, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPlayerData>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  findPlayerDataByUserId(id: number): Observable<EntityResponseType> {
    return this.http.get<IPlayerData>(`${this.resourceUrl}/user/${id}`, { observe: 'response' });
  }

  findPlayerDataByName(name: String) {
    return this.http.get<IPlayerData>(`${this.resourceUrl}/search/${name}`, { observe: 'response' });
  }

  findPlayerDataByNameLike(name: String) {
    return this.http.get<IPlayerData[]>(`${this.resourceUrl}/search-like/${name}`, { observe: 'response' });
  }
  findPlayerDataByNameLikeWithTeams(name: String) {
    return this.http.get<IPlayerData[]>(`${this.resourceUrl}/search-like-team/${name}`, { observe: 'response' });
  }

  findAllPlayerDataByTeamId(id: number): Observable<EntityArrayResponseType> {
    return this.http.get<IPlayerData[]>(`${this.resourceUrl}/team/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPlayerData[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  getPlayerDataByPlayerIds(playerIds: number[]): Observable<IPlayerData[]> {
    const params = { playerIds: playerIds.join(',') }; // Convert array to comma-separated string
    return this.http.get<IPlayerData[]>(`${this.resourceUrl}/list/`, { params });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPlayerDataIdentifier(playerData: Pick<IPlayerData, 'id'>): number {
    return playerData.id;
  }

  comparePlayerData(o1: Pick<IPlayerData, 'id'> | null, o2: Pick<IPlayerData, 'id'> | null): boolean {
    return o1 && o2 ? this.getPlayerDataIdentifier(o1) === this.getPlayerDataIdentifier(o2) : o1 === o2;
  }

  addPlayerDataToCollectionIfMissing<Type extends Pick<IPlayerData, 'id'>>(
    playerDataCollection: Type[],
    ...playerDataToCheck: (Type | null | undefined)[]
  ): Type[] {
    const playerData: Type[] = playerDataToCheck.filter(isPresent);
    if (playerData.length > 0) {
      const playerDataCollectionIdentifiers = playerDataCollection.map(playerDataItem => this.getPlayerDataIdentifier(playerDataItem)!);
      const playerDataToAdd = playerData.filter(playerDataItem => {
        const playerDataIdentifier = this.getPlayerDataIdentifier(playerDataItem);
        if (playerDataCollectionIdentifiers.includes(playerDataIdentifier)) {
          return false;
        }
        playerDataCollectionIdentifiers.push(playerDataIdentifier);
        return true;
      });
      return [...playerDataToAdd, ...playerDataCollection];
    }
    return playerDataCollection;
  }
}
