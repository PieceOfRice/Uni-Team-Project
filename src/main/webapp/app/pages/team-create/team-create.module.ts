import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { SharedModule } from 'app/shared/shared.module';
import { TeamCreateComponent } from './team-create.component';

@NgModule({
  imports: [CommonModule, SharedModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatDialogModule],
  declarations: [TeamCreateComponent],
})
export class TeamCreateModule {}
