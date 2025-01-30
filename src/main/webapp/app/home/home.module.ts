import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { MapRoleAuthority } from './map-role-authority.pipe';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';

@NgModule({
  imports: [CommonModule, SharedModule, RouterModule.forChild([HOME_ROUTE]), MatPaginatorModule, MatCardModule],
  providers: [TitleCasePipe],
  declarations: [HomeComponent, MapRoleAuthority],
})
export class HomeModule {}
