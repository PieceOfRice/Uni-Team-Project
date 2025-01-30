import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGame } from '../game.model';
import { GameService } from '../service/game.service';

@Injectable({ providedIn: 'root' })
export class GameRoutingResolveService implements Resolve<IGame | null> {
  constructor(protected service: GameService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGame | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((game: HttpResponse<IGame>) => {
          if (game.body) {
            return of(game.body);
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
