import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGame } from '../game.model';

@Component({
  selector: 'jhi-game-detail',
  templateUrl: './game-detail.component.html',
})
export class GameDetailComponent implements OnInit {
  game: IGame | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ game }) => {
      this.game = game;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
