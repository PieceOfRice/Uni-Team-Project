import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OverwatchMapComponent } from './list/overwatch-map.component';
import { OverwatchMapDetailComponent } from './detail/overwatch-map-detail.component';
import { OverwatchMapUpdateComponent } from './update/overwatch-map-update.component';
import { OverwatchMapDeleteDialogComponent } from './delete/overwatch-map-delete-dialog.component';
import { OverwatchMapRoutingModule } from './route/overwatch-map-routing.module';

@NgModule({
  imports: [SharedModule, OverwatchMapRoutingModule],
  declarations: [OverwatchMapComponent, OverwatchMapDetailComponent, OverwatchMapUpdateComponent, OverwatchMapDeleteDialogComponent],
})
export class OverwatchMapModule {}
