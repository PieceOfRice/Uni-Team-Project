import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OverwatchMapComponent } from '../list/overwatch-map.component';
import { OverwatchMapDetailComponent } from '../detail/overwatch-map-detail.component';
import { OverwatchMapUpdateComponent } from '../update/overwatch-map-update.component';
import { OverwatchMapRoutingResolveService } from './overwatch-map-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const overwatchMapRoute: Routes = [
  {
    path: '',
    component: OverwatchMapComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OverwatchMapDetailComponent,
    resolve: {
      overwatchMap: OverwatchMapRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OverwatchMapUpdateComponent,
    resolve: {
      overwatchMap: OverwatchMapRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OverwatchMapUpdateComponent,
    resolve: {
      overwatchMap: OverwatchMapRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(overwatchMapRoute)],
  exports: [RouterModule],
})
export class OverwatchMapRoutingModule {}
