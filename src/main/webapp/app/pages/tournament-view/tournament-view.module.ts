import { NgModule } from '@angular/core';
import { CommonModule, UpperCasePipe } from '@angular/common';
import { TournamentViewComponent } from './tournament-view.component';
import { RouterModule } from '@angular/router';
import { SharedModule } from 'app/shared/shared.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { DisplayMoneyPipe } from './pipes/display-money.pipe';
import { DisplayBracketFormatPipe } from './pipes/display-bracket-format.pipe';
import { DisplayParticipantsPipe } from './pipes/display-participants.pipe';
import { DisplayStatusPipe } from './pipes/display-status.pipe';
import { DisplayAccessStatusPipe } from './pipes/display-access-status.pipe';
import { DisplayTimePipe } from './pipes/display-time.pipe';
import { DisplaySettingPipe } from './pipes/display-setting.pipe';
import { DisplayGamesPerMatchPipe } from './pipes/display-games-per-match.pipe';
import { ParticipantComponent } from './participant/participant.component';
import { OrganiserNamePipe } from './pipes/organiser-name.pipe';
import { BracketComponent } from './bracket/bracket.component';
import { BracketLineComponent } from './bracket-line/bracket-line.component';
import { DisplayLocationPipe } from './pipes/display-location.pipe';

@NgModule({
  declarations: [
    TournamentViewComponent,
    DisplayMoneyPipe,
    DisplayBracketFormatPipe,
    DisplayParticipantsPipe,
    DisplayStatusPipe,
    DisplayAccessStatusPipe,
    DisplayTimePipe,
    DisplaySettingPipe,
    DisplayGamesPerMatchPipe,
    ParticipantComponent,
    OrganiserNamePipe,
    BracketComponent,
    BracketLineComponent,
    DisplayLocationPipe,
  ],
  imports: [CommonModule, SharedModule, RouterModule, FontAwesomeModule, UpperCasePipe],
})
export class TournamentViewModule {}
