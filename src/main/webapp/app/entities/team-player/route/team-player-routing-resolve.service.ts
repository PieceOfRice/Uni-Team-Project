import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeamPlayer } from '../team-player.model';
import { TeamPlayerService } from '../service/team-player.service';

@Injectable({ providedIn: 'root' })
export class TeamPlayerRoutingResolveService implements Resolve<ITeamPlayer | null> {
  constructor(protected service: TeamPlayerService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITeamPlayer | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((teamPlayer: HttpResponse<ITeamPlayer>) => {
          if (teamPlayer.body) {
            return of(teamPlayer.body);
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
