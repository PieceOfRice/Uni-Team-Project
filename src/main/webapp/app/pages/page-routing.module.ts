import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TeamCreateComponent } from './team-create/team-create.component';
import { SearchComponent } from './search/search.component';
import { TournamentCreateComponent } from './tournament-create/tournament-create.component';
import { TeamViewComponent } from './team-view/team-view.component';
import { MatchComponent } from './match/match.component';
import { PlayerComponent } from './player/player.component';
import { TeamRoutingResolveService } from 'app/entities/team/route/team-routing-resolve.service';
import { TournamentViewComponent } from './tournament-view/tournament-view.component';
import { MatchSubmitComponent } from './match-submit/match-submit.component';
import { TournamentEditComponent } from './tournament-edit/tournament-edit.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'team-create',
        data: { pageTitle: 'Create Team' },
        component: TeamCreateComponent,
        loadChildren: () => import('./team-create/team-create.module').then(m => m.TeamCreateModule),
      },
      {
        path: 'team-view/:id',
        data: { pageTitle: 'View Team' },
        resolve: {
          team: TeamRoutingResolveService,
        },
        component: TeamViewComponent,
        loadChildren: () => import('./team-view/team-view.module').then(m => m.TeamViewModule),
      },
      {
        path: 'search',
        data: { pageTitle: 'Search' },
        component: SearchComponent,
        loadChildren: () => import('./search/search.module').then(m => m.SearchModule),
      },
      {
        path: 'tournament-create',
        data: { pageTitle: 'Create Tournament' },
        component: TournamentCreateComponent,
        loadChildren: () => import('./tournament-create/tournament-create.module').then(m => m.TournamentCreateModule),
      },
      {
        path: 'tournament-edit/:id',
        data: { pageTitle: 'Edit Tournament' },
        component: TournamentEditComponent,
        loadChildren: () => import('./tournament-edit/tournament-edit.module').then(m => m.TournamentEditModule),
      },
      {
        path: 'match-view/:id',
        data: { pageTitle: 'Match' },
        component: MatchComponent,
        loadChildren: () => import('./match/match.module').then(m => m.MatchModule),
      },
      {
        path: 'player/:id',
        data: { pageTitle: 'Player' },
        component: PlayerComponent,
        loadChildren: () => import('./player/player.module').then(m => m.PlayerModule),
      },
      {
        path: 'tournament-view/:id',
        data: { pageTitle: 'Tournament' },
        component: TournamentViewComponent,
        loadChildren: () => import('./tournament-view/tournament-view.module').then(m => m.TournamentViewModule),
      },
      {
        path: 'match-submit/:id',
        data: { pageTitle: 'Submit Scores' },
        component: MatchSubmitComponent,
        loadChildren: () => import('./match-submit/match-submit.module').then(m => m.MatchSubmitModule),
      },
    ]),
  ],
  exports: [RouterModule],
  declarations: [],
})
export class PageRoutingModule {}
