import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlayerData } from '../player-data.model';
import { PlayerDataService } from '../service/player-data.service';

@Injectable({ providedIn: 'root' })
export class PlayerDataRoutingResolveService implements Resolve<IPlayerData | null> {
  constructor(protected service: PlayerDataService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPlayerData | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((playerData: HttpResponse<IPlayerData>) => {
          if (playerData.body) {
            return of(playerData.body);
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
