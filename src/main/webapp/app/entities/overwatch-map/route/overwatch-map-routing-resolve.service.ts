import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOverwatchMap } from '../overwatch-map.model';
import { OverwatchMapService } from '../service/overwatch-map.service';

@Injectable({ providedIn: 'root' })
export class OverwatchMapRoutingResolveService implements Resolve<IOverwatchMap | null> {
  constructor(protected service: OverwatchMapService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOverwatchMap | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((overwatchMap: HttpResponse<IOverwatchMap>) => {
          if (overwatchMap.body) {
            return of(overwatchMap.body);
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
