import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { GameFormService, GameFormGroup } from './game-form.service';
import { IGame } from '../game.model';
import { GameService } from '../service/game.service';
import { IOverwatchMap } from 'app/entities/overwatch-map/overwatch-map.model';
import { OverwatchMapService } from 'app/entities/overwatch-map/service/overwatch-map.service';
import { IMatch } from 'app/entities/match/match.model';
import { MatchService } from 'app/entities/match/service/match.service';

@Component({
  selector: 'jhi-game-update',
  templateUrl: './game-update.component.html',
})
export class GameUpdateComponent implements OnInit {
  isSaving = false;
  game: IGame | null = null;

  overwatchMapsCollection: IOverwatchMap[] = [];
  matchesSharedCollection: IMatch[] = [];

  editForm: GameFormGroup = this.gameFormService.createGameFormGroup();

  constructor(
    protected gameService: GameService,
    protected gameFormService: GameFormService,
    protected overwatchMapService: OverwatchMapService,
    protected matchService: MatchService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOverwatchMap = (o1: IOverwatchMap | null, o2: IOverwatchMap | null): boolean =>
    this.overwatchMapService.compareOverwatchMap(o1, o2);

  compareMatch = (o1: IMatch | null, o2: IMatch | null): boolean => this.matchService.compareMatch(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ game }) => {
      this.game = game;
      if (game) {
        this.updateForm(game);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const game = this.gameFormService.getGame(this.editForm);
    if (game.id !== null) {
      this.subscribeToSaveResponse(this.gameService.update(game));
    } else {
      this.subscribeToSaveResponse(this.gameService.create(game));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGame>>): void {
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

  protected updateForm(game: IGame): void {
    this.game = game;
    this.gameFormService.resetForm(this.editForm, game);

    this.overwatchMapsCollection = this.overwatchMapService.addOverwatchMapToCollectionIfMissing<IOverwatchMap>(
      this.overwatchMapsCollection,
      game.overwatchMap
    );
    this.matchesSharedCollection = this.matchService.addMatchToCollectionIfMissing<IMatch>(this.matchesSharedCollection, game.match);
  }

  protected loadRelationshipsOptions(): void {
    this.overwatchMapService
      .query({ filter: 'game-is-null' })
      .pipe(map((res: HttpResponse<IOverwatchMap[]>) => res.body ?? []))
      .pipe(
        map((overwatchMaps: IOverwatchMap[]) =>
          this.overwatchMapService.addOverwatchMapToCollectionIfMissing<IOverwatchMap>(overwatchMaps, this.game?.overwatchMap)
        )
      )
      .subscribe((overwatchMaps: IOverwatchMap[]) => (this.overwatchMapsCollection = overwatchMaps));

    this.matchService
      .query()
      .pipe(map((res: HttpResponse<IMatch[]>) => res.body ?? []))
      .pipe(map((matches: IMatch[]) => this.matchService.addMatchToCollectionIfMissing<IMatch>(matches, this.game?.match)))
      .subscribe((matches: IMatch[]) => (this.matchesSharedCollection = matches));
  }
}
