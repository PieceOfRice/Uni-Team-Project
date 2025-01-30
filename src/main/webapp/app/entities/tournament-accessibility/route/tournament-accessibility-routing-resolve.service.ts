import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITournamentAccessibility } from '../tournament-accessibility.model';
import { TournamentAccessibilityService } from '../service/tournament-accessibility.service';

@Injectable({ providedIn: 'root' })
export class TournamentAccessibilityRoutingResolveService implements Resolve<ITournamentAccessibility | null> {
  constructor(protected service: TournamentAccessibilityService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITournamentAccessibility | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tournamentAccessibility: HttpResponse<ITournamentAccessibility>) => {
          if (tournamentAccessibility.body) {
            return of(tournamentAccessibility.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
