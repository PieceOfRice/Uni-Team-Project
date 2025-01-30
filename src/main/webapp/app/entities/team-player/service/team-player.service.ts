import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITeamPlayer, NewTeamPlayer } from '../team-player.model';

export type PartialUpdateTeamPlayer = Partial<ITeamPlayer> & Pick<ITeamPlayer, 'id'>;

export type EntityResponseType = HttpResponse<ITeamPlayer>;
export type EntityArrayResponseType = HttpResponse<ITeamPlayer[]>;

@Injectable({ providedIn: 'root' })
export class TeamPlayerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/team-players');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(teamPlayer: NewTeamPlayer): Observable<EntityResponseType> {
    return this.http.post<ITeamPlayer>(this.resourceUrl, teamPlayer, { observe: 'response' });
  }

  update(teamPlayer: ITeamPlayer): Observable<EntityResponseType> {
    return this.http.put<ITeamPlayer>(`${this.resourceUrl}/${this.getTeamPlayerIdentifier(teamPlayer)}`, teamPlayer, {
      observe: 'response',
    });
  }

  partialUpdate(teamPlayer: PartialUpdateTeamPlayer): Observable<EntityResponseType> {
    return this.http.patch<ITeamPlayer>(`${this.resourceUrl}/${this.getTeamPlayerIdentifier(teamPlayer)}`, teamPlayer, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeamPlayer>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  findByPlayerDataIdAndTeamId(pdid: number, teamid: number): Observable<EntityResponseType> {
    return this.http.get<ITeamPlayer>(`${this.resourceUrl}/${pdid}/${teamid}`, { observe: 'response' });
  }

  getTeamPlayerByPlayerId(playerId: number): Observable<EntityResponseType> {
    return this.http.get<ITeamPlayer>(`${this.resourceUrl}/player/${playerId}`, { observe: 'response' });
  }

  //get all team players given a teamId
  getTeamPlayersByTeamId(teamId: number): Observable<any> {
    const url = `${this.resourceUrl}/team/${teamId}`;
    return this.http.get<any>(url);
  }

  findLeader(pdid: number): Observable<EntityArrayResponseType> {
    return this.http.get<ITeamPlayer[]>(`${this.resourceUrl}/leader/${pdid}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeamPlayer[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTeamPlayerIdentifier(teamPlayer: Pick<ITeamPlayer, 'id'>): number {
    return teamPlayer.id;
  }

  compareTeamPlayer(o1: Pick<ITeamPlayer, 'id'> | null, o2: Pick<ITeamPlayer, 'id'> | null): boolean {
    return o1 && o2 ? this.getTeamPlayerIdentifier(o1) === this.getTeamPlayerIdentifier(o2) : o1 === o2;
  }

  addTeamPlayerToCollectionIfMissing<Type extends Pick<ITeamPlayer, 'id'>>(
    teamPlayerCollection: Type[],
    ...teamPlayersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const teamPlayers: Type[] = teamPlayersToCheck.filter(isPresent);
    if (teamPlayers.length > 0) {
      const teamPlayerCollectionIdentifiers = teamPlayerCollection.map(teamPlayerItem => this.getTeamPlayerIdentifier(teamPlayerItem)!);
      const teamPlayersToAdd = teamPlayers.filter(teamPlayerItem => {
        const teamPlayerIdentifier = this.getTeamPlayerIdentifier(teamPlayerItem);
        if (teamPlayerCollectionIdentifiers.includes(teamPlayerIdentifier)) {
          return false;
        }
        teamPlayerCollectionIdentifiers.push(teamPlayerIdentifier);
        return true;
      });
      return [...teamPlayersToAdd, ...teamPlayerCollection];
    }
    return teamPlayerCollection;
  }
}
