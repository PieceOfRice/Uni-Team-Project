import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITournamentAccessibility, NewTournamentAccessibility } from '../tournament-accessibility.model';

export type PartialUpdateTournamentAccessibility = Partial<ITournamentAccessibility> & Pick<ITournamentAccessibility, 'id'>;

export type EntityResponseType = HttpResponse<ITournamentAccessibility>;
export type EntityArrayResponseType = HttpResponse<ITournamentAccessibility[]>;

@Injectable({ providedIn: 'root' })
export class TournamentAccessibilityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tournament-accessibilities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tournamentAccessibility: NewTournamentAccessibility): Observable<EntityResponseType> {
    console.log(tournamentAccessibility);
    return this.http.post<ITournamentAccessibility>(this.resourceUrl, tournamentAccessibility, { observe: 'response' });
  }

  update(tournamentAccessibility: ITournamentAccessibility): Observable<EntityResponseType> {
    return this.http.put<ITournamentAccessibility>(
      `${this.resourceUrl}/${this.getTournamentAccessibilityIdentifier(tournamentAccessibility)}`,
      tournamentAccessibility,
      { observe: 'response' }
    );
  }

  partialUpdate(tournamentAccessibility: PartialUpdateTournamentAccessibility): Observable<EntityResponseType> {
    return this.http.patch<ITournamentAccessibility>(
      `${this.resourceUrl}/${this.getTournamentAccessibilityIdentifier(tournamentAccessibility)}`,
      tournamentAccessibility,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITournamentAccessibility>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITournamentAccessibility[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTournamentAccessibilityIdentifier(tournamentAccessibility: Pick<ITournamentAccessibility, 'id'>): number {
    return tournamentAccessibility.id;
  }

  compareTournamentAccessibility(
    o1: Pick<ITournamentAccessibility, 'id'> | null,
    o2: Pick<ITournamentAccessibility, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getTournamentAccessibilityIdentifier(o1) === this.getTournamentAccessibilityIdentifier(o2) : o1 === o2;
  }

  addTournamentAccessibilityToCollectionIfMissing<Type extends Pick<ITournamentAccessibility, 'id'>>(
    tournamentAccessibilityCollection: Type[],
    ...tournamentAccessibilitiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tournamentAccessibilities: Type[] = tournamentAccessibilitiesToCheck.filter(isPresent);
    if (tournamentAccessibilities.length > 0) {
      const tournamentAccessibilityCollectionIdentifiers = tournamentAccessibilityCollection.map(
        tournamentAccessibilityItem => this.getTournamentAccessibilityIdentifier(tournamentAccessibilityItem)!
      );
      const tournamentAccessibilitiesToAdd = tournamentAccessibilities.filter(tournamentAccessibilityItem => {
        const tournamentAccessibilityIdentifier = this.getTournamentAccessibilityIdentifier(tournamentAccessibilityItem);
        if (tournamentAccessibilityCollectionIdentifiers.includes(tournamentAccessibilityIdentifier)) {
          return false;
        }
        tournamentAccessibilityCollectionIdentifiers.push(tournamentAccessibilityIdentifier);
        return true;
      });
      return [...tournamentAccessibilitiesToAdd, ...tournamentAccessibilityCollection];
    }
    return tournamentAccessibilityCollection;
  }
}
