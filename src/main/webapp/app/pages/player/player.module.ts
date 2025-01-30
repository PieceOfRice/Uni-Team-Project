import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlayerComponent } from './player.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [PlayerComponent],
  imports: [CommonModule, FontAwesomeModule, RouterModule],
})
export class PlayerModule {}
