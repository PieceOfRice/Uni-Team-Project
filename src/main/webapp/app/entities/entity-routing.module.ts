import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'tournament',
        data: { pageTitle: 'Tournaments' },
        loadChildren: () => import('./tournament/tournament.module').then(m => m.TournamentModule),
      },
      {
        path: 'team',
        data: { pageTitle: 'Teams' },
        loadChildren: () => import('./team/team.module').then(m => m.TeamModule),
      },
      {
        path: 'match',
        data: { pageTitle: 'Matches' },
        loadChildren: () => import('./match/match.module').then(m => m.MatchModule),
      },
      {
        path: 'game',
        data: { pageTitle: 'Games' },
        loadChildren: () => import('./game/game.module').then(m => m.GameModule),
      },
      {
        path: 'player-data',
        data: { pageTitle: 'PlayerData' },
        loadChildren: () => import('./player-data/player-data.module').then(m => m.PlayerDataModule),
      },
      {
        path: 'game-player',
        data: { pageTitle: 'GamePlayers' },
        loadChildren: () => import('./game-player/game-player.module').then(m => m.GamePlayerModule),
      },
      {
        path: 'overwatch-map',
        data: { pageTitle: 'OverwatchMaps' },
        loadChildren: () => import('./overwatch-map/overwatch-map.module').then(m => m.OverwatchMapModule),
      },
      {
        path: 'participant',
        data: { pageTitle: 'Participants' },
        loadChildren: () => import('./participant/participant.module').then(m => m.ParticipantModule),
      },
      {
        path: 'team-player',
        data: { pageTitle: 'TeamPlayers' },
        loadChildren: () => import('./team-player/team-player.module').then(m => m.TeamPlayerModule),
      },
      {
        path: 'tournament-accessibility',
        data: { pageTitle: 'TournamentAccessibilities' },
        loadChildren: () => import('./tournament-accessibility/tournament-accessibility.module').then(m => m.TournamentAccessibilityModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
