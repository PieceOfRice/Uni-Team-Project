import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ParticipantFormService, ParticipantFormGroup } from './participant-form.service';
import { IParticipant } from '../participant.model';
import { ParticipantService } from '../service/participant.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { TournamentService } from 'app/entities/tournament/service/tournament.service';

@Component({
  selector: 'jhi-participant-update',
  templateUrl: './participant-update.component.html',
})
export class ParticipantUpdateComponent implements OnInit {
  isSaving = false;
  participant: IParticipant | null = null;

  teamsSharedCollection: ITeam[] = [];
  tournamentsSharedCollection: ITournament[] = [];

  editForm: ParticipantFormGroup = this.participantFormService.createParticipantFormGroup();

  constructor(
    protected participantService: ParticipantService,
    protected participantFormService: ParticipantFormService,
    protected teamService: TeamService,
    protected tournamentService: TournamentService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTeam = (o1: ITeam | null, o2: ITeam | null): boolean => this.teamService.compareTeam(o1, o2);

  compareTournament = (o1: ITournament | null, o2: ITournament | null): boolean => this.tournamentService.compareTournament(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ participant }) => {
      this.participant = participant;
      if (participant) {
        this.updateForm(participant);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const participant = this.participantFormService.getParticipant(this.editForm);
    if (participant.id !== null) {
      this.subscribeToSaveResponse(this.participantService.update(participant));
    } else {
      this.subscribeToSaveResponse(this.participantService.create(participant));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IParticipant>>): void {
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

  protected updateForm(participant: IParticipant): void {
    this.participant = participant;
    this.participantFormService.resetForm(this.editForm, participant);

    this.teamsSharedCollection = this.teamService.addTeamToCollectionIfMissing<ITeam>(this.teamsSharedCollection, participant.team);
    this.tournamentsSharedCollection = this.tournamentService.addTournamentToCollectionIfMissing<ITournament>(
      this.tournamentsSharedCollection,
      participant.tournament
    );
  }

  protected loadRelationshipsOptions(): void {
    this.teamService
      .query()
      .pipe(map((res: HttpResponse<ITeam[]>) => res.body ?? []))
      .pipe(map((teams: ITeam[]) => this.teamService.addTeamToCollectionIfMissing<ITeam>(teams, this.participant?.team)))
      .subscribe((teams: ITeam[]) => (this.teamsSharedCollection = teams));

    this.tournamentService
      .query()
      .pipe(map((res: HttpResponse<ITournament[]>) => res.body ?? []))
      .pipe(
        map((tournaments: ITournament[]) =>
          this.tournamentService.addTournamentToCollectionIfMissing<ITournament>(tournaments, this.participant?.tournament)
        )
      )
      .subscribe((tournaments: ITournament[]) => (this.tournamentsSharedCollection = tournaments));
  }
}
