import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TeamPlayerFormService, TeamPlayerFormGroup } from './team-player-form.service';
import { ITeamPlayer } from '../team-player.model';
import { TeamPlayerService } from '../service/team-player.service';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';
import { TeamRole } from 'app/entities/enumerations/team-role.model';

@Component({
  selector: 'jhi-team-player-update',
  templateUrl: './team-player-update.component.html',
})
export class TeamPlayerUpdateComponent implements OnInit {
  isSaving = false;
  teamPlayer: ITeamPlayer | null = null;
  teamRoleValues = Object.keys(TeamRole);

  playerDataSharedCollection: IPlayerData[] = [];
  teamsSharedCollection: ITeam[] = [];

  editForm: TeamPlayerFormGroup = this.teamPlayerFormService.createTeamPlayerFormGroup();

  constructor(
    protected teamPlayerService: TeamPlayerService,
    protected teamPlayerFormService: TeamPlayerFormService,
    protected playerDataService: PlayerDataService,
    protected teamService: TeamService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePlayerData = (o1: IPlayerData | null, o2: IPlayerData | null): boolean => this.playerDataService.comparePlayerData(o1, o2);

  compareTeam = (o1: ITeam | null, o2: ITeam | null): boolean => this.teamService.compareTeam(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamPlayer }) => {
      this.teamPlayer = teamPlayer;
      if (teamPlayer) {
        this.updateForm(teamPlayer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teamPlayer = this.teamPlayerFormService.getTeamPlayer(this.editForm);
    if (teamPlayer.id !== null) {
      this.subscribeToSaveResponse(this.teamPlayerService.update(teamPlayer));
    } else {
      this.subscribeToSaveResponse(this.teamPlayerService.create(teamPlayer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamPlayer>>): void {
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

  protected updateForm(teamPlayer: ITeamPlayer): void {
    this.teamPlayer = teamPlayer;
    this.teamPlayerFormService.resetForm(this.editForm, teamPlayer);

    this.playerDataSharedCollection = this.playerDataService.addPlayerDataToCollectionIfMissing<IPlayerData>(
      this.playerDataSharedCollection,
      teamPlayer.player
    );
    this.teamsSharedCollection = this.teamService.addTeamToCollectionIfMissing<ITeam>(this.teamsSharedCollection, teamPlayer.team);
  }

  protected loadRelationshipsOptions(): void {
    this.playerDataService
      .query()
      .pipe(map((res: HttpResponse<IPlayerData[]>) => res.body ?? []))
      .pipe(
        map((playerData: IPlayerData[]) =>
          this.playerDataService.addPlayerDataToCollectionIfMissing<IPlayerData>(playerData, this.teamPlayer?.player)
        )
      )
      .subscribe((playerData: IPlayerData[]) => (this.playerDataSharedCollection = playerData));

    this.teamService
      .query()
      .pipe(map((res: HttpResponse<ITeam[]>) => res.body ?? []))
      .pipe(map((teams: ITeam[]) => this.teamService.addTeamToCollectionIfMissing<ITeam>(teams, this.teamPlayer?.team)))
      .subscribe((teams: ITeam[]) => (this.teamsSharedCollection = teams));
  }
}
