import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TournamentAccessibilityFormService, TournamentAccessibilityFormGroup } from './tournament-accessibility-form.service';
import { ITournamentAccessibility } from '../tournament-accessibility.model';
import { TournamentAccessibilityService } from '../service/tournament-accessibility.service';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { TournamentService } from 'app/entities/tournament/service/tournament.service';
import { VenueAccessibilities } from 'app/entities/enumerations/venue-accessibilities.model';

@Component({
  selector: 'jhi-tournament-accessibility-update',
  templateUrl: './tournament-accessibility-update.component.html',
})
export class TournamentAccessibilityUpdateComponent implements OnInit {
  isSaving = false;
  tournamentAccessibility: ITournamentAccessibility | null = null;
  venueAccessibilitiesValues = Object.keys(VenueAccessibilities);

  tournamentsSharedCollection: ITournament[] = [];

  editForm: TournamentAccessibilityFormGroup = this.tournamentAccessibilityFormService.createTournamentAccessibilityFormGroup();

  constructor(
    protected tournamentAccessibilityService: TournamentAccessibilityService,
    protected tournamentAccessibilityFormService: TournamentAccessibilityFormService,
    protected tournamentService: TournamentService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTournament = (o1: ITournament | null, o2: ITournament | null): boolean => this.tournamentService.compareTournament(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tournamentAccessibility }) => {
      this.tournamentAccessibility = tournamentAccessibility;
      if (tournamentAccessibility) {
        this.updateForm(tournamentAccessibility);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tournamentAccessibility = this.tournamentAccessibilityFormService.getTournamentAccessibility(this.editForm);
    if (tournamentAccessibility.id !== null) {
      this.subscribeToSaveResponse(this.tournamentAccessibilityService.update(tournamentAccessibility));
    } else {
      this.subscribeToSaveResponse(this.tournamentAccessibilityService.create(tournamentAccessibility));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITournamentAccessibility>>): void {
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

  protected updateForm(tournamentAccessibility: ITournamentAccessibility): void {
    this.tournamentAccessibility = tournamentAccessibility;
    this.tournamentAccessibilityFormService.resetForm(this.editForm, tournamentAccessibility);

    this.tournamentsSharedCollection = this.tournamentService.addTournamentToCollectionIfMissing<ITournament>(
      this.tournamentsSharedCollection,
      tournamentAccessibility.tournament
    );
  }

  protected loadRelationshipsOptions(): void {
    this.tournamentService
      .query()
      .pipe(map((res: HttpResponse<ITournament[]>) => res.body ?? []))
      .pipe(
        map((tournaments: ITournament[]) =>
          this.tournamentService.addTournamentToCollectionIfMissing<ITournament>(tournaments, this.tournamentAccessibility?.tournament)
        )
      )
      .subscribe((tournaments: ITournament[]) => (this.tournamentsSharedCollection = tournaments));
  }
}
