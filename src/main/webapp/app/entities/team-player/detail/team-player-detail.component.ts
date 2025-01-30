import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITeamPlayer } from '../team-player.model';

@Component({
  selector: 'jhi-team-player-detail',
  templateUrl: './team-player-detail.component.html',
})
export class TeamPlayerDetailComponent implements OnInit {
  teamPlayer: ITeamPlayer | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamPlayer }) => {
      this.teamPlayer = teamPlayer;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
