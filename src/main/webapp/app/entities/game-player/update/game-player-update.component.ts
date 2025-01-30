import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { GamePlayerFormService, GamePlayerFormGroup } from './game-player-form.service';
import { IGamePlayer } from '../game-player.model';
import { GamePlayerService } from '../service/game-player.service';
import { IGame } from 'app/entities/game/game.model';
import { GameService } from 'app/entities/game/service/game.service';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';
import { GameTeam } from 'app/entities/enumerations/game-team.model';

@Component({
  selector: 'jhi-game-player-update',
  templateUrl: './game-player-update.component.html',
})
export class GamePlayerUpdateComponent implements OnInit {
  isSaving = false;
  gamePlayer: IGamePlayer | null = null;
  gameTeamValues = Object.keys(GameTeam);

  gamesSharedCollection: IGame[] = [];
  playerDataSharedCollection: IPlayerData[] = [];

  editForm: GamePlayerFormGroup = this.gamePlayerFormService.createGamePlayerFormGroup();

  constructor(
    protected gamePlayerService: GamePlayerService,
    protected gamePlayerFormService: GamePlayerFormService,
    protected gameService: GameService,
    protected playerDataService: PlayerDataService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareGame = (o1: IGame | null, o2: IGame | null): boolean => this.gameService.compareGame(o1, o2);

  comparePlayerData = (o1: IPlayerData | null, o2: IPlayerData | null): boolean => this.playerDataService.comparePlayerData(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gamePlayer }) => {
      this.gamePlayer = gamePlayer;
      if (gamePlayer) {
        this.updateForm(gamePlayer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const gamePlayer = this.gamePlayerFormService.getGamePlayer(this.editForm);
    if (gamePlayer.id !== null) {
      this.subscribeToSaveResponse(this.gamePlayerService.update(gamePlayer));
    } else {
      this.subscribeToSaveResponse(this.gamePlayerService.create(gamePlayer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGamePlayer>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(gamePlayer: IGamePlayer): void {
    this.gamePlayer = gamePlayer;
    this.gamePlayerFormService.resetForm(this.editForm, gamePlayer);

    this.gamesSharedCollection = this.gameService.addGameToCollectionIfMissing<IGame>(this.gamesSharedCollection, gamePlayer.game);
    this.playerDataSharedCollection = this.playerDataService.addPlayerDataToCollectionIfMissing<IPlayerData>(
      this.playerDataSharedCollection,
      gamePlayer.playerData
    );
  }

  protected loadRelationshipsOptions(): void {
    this.gameService
      .query()
      .pipe(map((res: HttpResponse<IGame[]>) => res.body ?? []))
      .pipe(map((games: IGame[]) => this.gameService.addGameToCollectionIfMissing<IGame>(games, this.gamePlayer?.game)))
      .subscribe((games: IGame[]) => (this.gamesSharedCollection = games));

    this.playerDataService
      .query()
      .pipe(map((res: HttpResponse<IPlayerData[]>) => res.body ?? []))
      .pipe(
        map((playerData: IPlayerData[]) =>
          this.playerDataService.addPlayerDataToCollectionIfMissing<IPlayerData>(playerData, this.gamePlayer?.playerData)
        )
      )
      .subscribe((playerData: IPlayerData[]) => (this.playerDataSharedCollection = playerData));
  }
}
