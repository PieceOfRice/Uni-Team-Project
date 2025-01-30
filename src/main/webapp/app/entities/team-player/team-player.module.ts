import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TeamPlayerComponent } from './list/team-player.component';
import { TeamPlayerDetailComponent } from './detail/team-player-detail.component';
import { TeamPlayerUpdateComponent } from './update/team-player-update.component';
import { TeamPlayerDeleteDialogComponent } from './delete/team-player-delete-dialog.component';
import { TeamPlayerRoutingModule } from './route/team-player-routing.module';

@NgModule({
  imports: [SharedModule, TeamPlayerRoutingModule],
  declarations: [TeamPlayerComponent, TeamPlayerDetailComponent, TeamPlayerUpdateComponent, TeamPlayerDeleteDialogComponent],
})
export class TeamPlayerModule {}
