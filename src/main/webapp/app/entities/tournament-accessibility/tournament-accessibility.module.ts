import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TournamentAccessibilityComponent } from './list/tournament-accessibility.component';
import { TournamentAccessibilityDetailComponent } from './detail/tournament-accessibility-detail.component';
import { TournamentAccessibilityUpdateComponent } from './update/tournament-accessibility-update.component';
import { TournamentAccessibilityDeleteDialogComponent } from './delete/tournament-accessibility-delete-dialog.component';
import { TournamentAccessibilityRoutingModule } from './route/tournament-accessibility-routing.module';

@NgModule({
  imports: [SharedModule, TournamentAccessibilityRoutingModule],
  declarations: [
    TournamentAccessibilityComponent,
    TournamentAccessibilityDetailComponent,
    TournamentAccessibilityUpdateComponent,
    TournamentAccessibilityDeleteDialogComponent,
  ],
})
export class TournamentAccessibilityModule {}
