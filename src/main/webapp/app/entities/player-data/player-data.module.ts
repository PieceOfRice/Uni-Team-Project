import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PlayerDataComponent } from './list/player-data.component';
import { PlayerDataDetailComponent } from './detail/player-data-detail.component';
import { PlayerDataUpdateComponent } from './update/player-data-update.component';
import { PlayerDataDeleteDialogComponent } from './delete/player-data-delete-dialog.component';
import { PlayerDataRoutingModule } from './route/player-data-routing.module';

@NgModule({
  imports: [SharedModule, PlayerDataRoutingModule],
  declarations: [PlayerDataComponent, PlayerDataDetailComponent, PlayerDataUpdateComponent, PlayerDataDeleteDialogComponent],
})
export class PlayerDataModule {}
