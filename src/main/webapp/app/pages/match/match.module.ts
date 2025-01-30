import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatchComponent } from './match.component';
import { Router, RouterModule } from '@angular/router';

import { FontAwesomeModule, FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { fontAwesomeIcons } from '../../config/font-awesome-icons';

@NgModule({
  declarations: [MatchComponent],
  imports: [CommonModule, FontAwesomeModule, RouterModule],
})
export class MatchModule {
  constructor(iconLibrary: FaIconLibrary) {
    iconLibrary.addIcons(...fontAwesomeIcons);
  }
}
