import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { MatchFormService, MatchFormGroup } from './match-form.service';
import { IMatch } from '../match.model';
import { MatchService } from '../service/match.service';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { TournamentService } from 'app/entities/tournament/service/tournament.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';

@Component({
  selector: 'jhi-match-update',
  templateUrl: './match-update.component.html',
})
export class MatchUpdateComponent implements OnInit {
  isSaving = false;
  match: IMatch | null = null;

  tournamentsSharedCollection: ITournament[] = [];
  teamsSharedCollection: ITeam[] = [];

  editForm: MatchFormGroup = this.matchFormService.createMatchFormGroup();

  constructor(
    protected matchService: MatchService,
    protected matchFormService: MatchFormService,
    protected tournamentService: TournamentService,
    protected teamService: TeamService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTournament = (o1: ITournament | null, o2: ITournament | null): boolean => this.tournamentService.compareTournament(o1, o2);

  compareTeam = (o1: ITeam | null, o2: ITeam | null): boolean => this.teamService.compareTeam(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ match }) => {
      this.match = match;
      if (match) {
        this.updateForm(match);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const match = this.matchFormService.getMatch(this.editForm);
    if (match.id !== null) {
      this.subscribeToSaveResponse(this.matchService.update(match));
    } else {
      this.subscribeToSaveResponse(this.matchService.create(match));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMatch>>): void {
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

  protected updateForm(match: IMatch): void {
    this.match = match;
    this.matchFormService.resetForm(this.editForm, match);

    this.tournamentsSharedCollection = this.tournamentService.addTournamentToCollectionIfMissing<ITournament>(
      this.tournamentsSharedCollection,
      match.tournament
    );
    this.teamsSharedCollection = this.teamService.addTeamToCollectionIfMissing<ITeam>(
      this.teamsSharedCollection,
      match.teamOne,
      match.teamTwo
    );
  }

  protected loadRelationshipsOptions(): void {
    this.tournamentService
      .query()
      .pipe(map((res: HttpResponse<ITournament[]>) => res.body ?? []))
      .pipe(
        map((tournaments: ITournament[]) =>
          this.tournamentService.addTournamentToCollectionIfMissing<ITournament>(tournaments, this.match?.tournament)
        )
      )
      .subscribe((tournaments: ITournament[]) => (this.tournamentsSharedCollection = tournaments));

    this.teamService
      .query()
      .pipe(map((res: HttpResponse<ITeam[]>) => res.body ?? []))
      .pipe(map((teams: ITeam[]) => this.teamService.addTeamToCollectionIfMissing<ITeam>(teams, this.match?.teamOne, this.match?.teamTwo)))
      .subscribe((teams: ITeam[]) => (this.teamsSharedCollection = teams));
  }
}
