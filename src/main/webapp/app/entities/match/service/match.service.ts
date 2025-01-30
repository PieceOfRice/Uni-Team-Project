import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMatch, NewMatch } from '../match.model';

export type PartialUpdateMatch = Partial<IMatch> & Pick<IMatch, 'id'>;

type RestOf<T extends IMatch | NewMatch> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

export type RestMatch = RestOf<IMatch>;

export type NewRestMatch = RestOf<NewMatch>;

export type PartialUpdateRestMatch = RestOf<PartialUpdateMatch>;

export type EntityResponseType = HttpResponse<IMatch>;
export type EntityArrayResponseType = HttpResponse<IMatch[]>;

@Injectable({ providedIn: 'root' })
export class MatchService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/matches');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(match: NewMatch): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(match);
    return this.http.post<RestMatch>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(match: IMatch): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(match);
    return this.http
      .put<RestMatch>(`${this.resourceUrl}/${this.getMatchIdentifier(match)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(match: PartialUpdateMatch): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(match);
    return this.http
      .patch<RestMatch>(`${this.resourceUrl}/${this.getMatchIdentifier(match)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMatch>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }
  findAllByTeamId(id: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<RestMatch[]>(`${this.resourceUrl}/team?teamId=${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }
  findAllByPlayerId(id: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<RestMatch[]>(`${this.resourceUrl}/player?id=${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }
  findAllByTournamentId(tournamentId: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<RestMatch[]>(`${this.resourceUrl}/tournament?id=${tournamentId}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  findFinalThreeMatchesByTournamentId(id: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<RestMatch[]>(`${this.resourceUrl}/search/tournament/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  findFinalMatchByTournamentId(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMatch>(`${this.resourceUrl}/search/tournament/final/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMatch[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMatchIdentifier(match: Pick<IMatch, 'id'>): number {
    return match.id;
  }

  compareMatch(o1: Pick<IMatch, 'id'> | null, o2: Pick<IMatch, 'id'> | null): boolean {
    return o1 && o2 ? this.getMatchIdentifier(o1) === this.getMatchIdentifier(o2) : o1 === o2;
  }

  addMatchToCollectionIfMissing<Type extends Pick<IMatch, 'id'>>(
    matchCollection: Type[],
    ...matchesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const matches: Type[] = matchesToCheck.filter(isPresent);
    if (matches.length > 0) {
      const matchCollectionIdentifiers = matchCollection.map(matchItem => this.getMatchIdentifier(matchItem)!);
      const matchesToAdd = matches.filter(matchItem => {
        const matchIdentifier = this.getMatchIdentifier(matchItem);
        if (matchCollectionIdentifiers.includes(matchIdentifier)) {
          return false;
        }
        matchCollectionIdentifiers.push(matchIdentifier);
        return true;
      });
      return [...matchesToAdd, ...matchCollection];
    }
    return matchCollection;
  }

  protected convertDateFromClient<T extends IMatch | NewMatch | PartialUpdateMatch>(match: T): RestOf<T> {
    return {
      ...match,
      startTime: match.startTime?.toJSON() ?? null,
      endTime: match.endTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restMatch: RestMatch): IMatch {
    return {
      ...restMatch,
      startTime: restMatch.startTime ? dayjs(restMatch.startTime) : undefined,
      endTime: restMatch.endTime ? dayjs(restMatch.endTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMatch>): HttpResponse<IMatch> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMatch[]>): HttpResponse<IMatch[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
