import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TeamPlayerComponent } from '../list/team-player.component';
import { TeamPlayerDetailComponent } from '../detail/team-player-detail.component';
import { TeamPlayerUpdateComponent } from '../update/team-player-update.component';
import { TeamPlayerRoutingResolveService } from './team-player-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const teamPlayerRoute: Routes = [
  {
    path: '',
    component: TeamPlayerComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TeamPlayerDetailComponent,
    resolve: {
      teamPlayer: TeamPlayerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TeamPlayerUpdateComponent,
    resolve: {
      teamPlayer: TeamPlayerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TeamPlayerUpdateComponent,
    resolve: {
      teamPlayer: TeamPlayerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(teamPlayerRoute)],
  exports: [RouterModule],
})
export class TeamPlayerRoutingModule {}
