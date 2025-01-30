import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TournamentFormService, TournamentFormGroup } from './tournament-form.service';
import { ITournament } from '../tournament.model';
import { TournamentService } from '../service/tournament.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';
import { TournamentBracketType } from 'app/entities/enumerations/tournament-bracket-type.model';
import { AccessStatus } from 'app/entities/enumerations/access-status.model';
import { TournamentSetting } from 'app/entities/enumerations/tournament-setting.model';

@Component({
  selector: 'jhi-tournament-update',
  templateUrl: './tournament-update.component.html',
})
export class TournamentUpdateComponent implements OnInit {
  isSaving = false;
  tournament: ITournament | null = null;
  tournamentBracketTypeValues = Object.keys(TournamentBracketType);
  accessStatusValues = Object.keys(AccessStatus);
  tournamentSettingValues = Object.keys(TournamentSetting);

  playerDataSharedCollection: IPlayerData[] = [];

  editForm: TournamentFormGroup = this.tournamentFormService.createTournamentFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected tournamentService: TournamentService,
    protected tournamentFormService: TournamentFormService,
    protected playerDataService: PlayerDataService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePlayerData = (o1: IPlayerData | null, o2: IPlayerData | null): boolean => this.playerDataService.comparePlayerData(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tournament }) => {
      this.tournament = tournament;
      if (tournament) {
        this.updateForm(tournament);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('teamprojectApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tournament = this.tournamentFormService.getTournament(this.editForm);
    if (tournament.id !== null) {
      this.subscribeToSaveResponse(this.tournamentService.update(tournament));
    } else {
      this.subscribeToSaveResponse(this.tournamentService.create(tournament));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITournament>>): void {
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

  protected updateForm(tournament: ITournament): void {
    this.tournament = tournament;
    this.tournamentFormService.resetForm(this.editForm, tournament);

    this.playerDataSharedCollection = this.playerDataService.addPlayerDataToCollectionIfMissing<IPlayerData>(
      this.playerDataSharedCollection,
      tournament.creator
    );
  }

  protected loadRelationshipsOptions(): void {
    this.playerDataService
      .query()
      .pipe(map((res: HttpResponse<IPlayerData[]>) => res.body ?? []))
      .pipe(
        map((playerData: IPlayerData[]) =>
          this.playerDataService.addPlayerDataToCollectionIfMissing<IPlayerData>(playerData, this.tournament?.creator)
        )
      )
      .subscribe((playerData: IPlayerData[]) => (this.playerDataSharedCollection = playerData));
  }
}
