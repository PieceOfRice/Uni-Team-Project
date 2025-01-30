import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IParticipant } from '../participant.model';
import { ParticipantService } from '../service/participant.service';

@Injectable({ providedIn: 'root' })
export class ParticipantRoutingResolveService implements Resolve<IParticipant | null> {
  constructor(protected service: ParticipantService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IParticipant | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((participant: HttpResponse<IParticipant>) => {
          if (participant.body) {
            return of(participant.body);
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
