import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TournamentAccessibilityComponent } from '../list/tournament-accessibility.component';
import { TournamentAccessibilityDetailComponent } from '../detail/tournament-accessibility-detail.component';
import { TournamentAccessibilityUpdateComponent } from '../update/tournament-accessibility-update.component';
import { TournamentAccessibilityRoutingResolveService } from './tournament-accessibility-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const tournamentAccessibilityRoute: Routes = [
  {
    path: '',
    component: TournamentAccessibilityComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TournamentAccessibilityDetailComponent,
    resolve: {
      tournamentAccessibility: TournamentAccessibilityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TournamentAccessibilityUpdateComponent,
    resolve: {
      tournamentAccessibility: TournamentAccessibilityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TournamentAccessibilityUpdateComponent,
    resolve: {
      tournamentAccessibility: TournamentAccessibilityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tournamentAccessibilityRoute)],
  exports: [RouterModule],
})
export class TournamentAccessibilityRoutingModule {}
