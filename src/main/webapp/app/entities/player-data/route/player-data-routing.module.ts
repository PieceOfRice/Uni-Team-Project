import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PlayerDataComponent } from '../list/player-data.component';
import { PlayerDataDetailComponent } from '../detail/player-data-detail.component';
import { PlayerDataUpdateComponent } from '../update/player-data-update.component';
import { PlayerDataRoutingResolveService } from './player-data-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const playerDataRoute: Routes = [
  {
    path: '',
    component: PlayerDataComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlayerDataDetailComponent,
    resolve: {
      playerData: PlayerDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlayerDataUpdateComponent,
    resolve: {
      playerData: PlayerDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlayerDataUpdateComponent,
    resolve: {
      playerData: PlayerDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(playerDataRoute)],
  exports: [RouterModule],
})
export class PlayerDataRoutingModule {}
