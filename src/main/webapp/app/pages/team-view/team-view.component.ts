import { AccessStatus } from 'app/entities/enumerations/access-status.model';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ITeam } from 'app/entities/team/team.model';
import { DataUtils } from 'app/core/util/data-util.service';
import { IMatch } from 'app/entities/match/match.model';
import { MatchService } from 'app/entities/match/service/match.service';
import { TeamService } from 'app/entities/team/service/team.service';
import { TeamPlayerService } from 'app/entities/team-player/service/team-player.service';
import { ITeamPlayer } from 'app/entities/team-player/team-player.model';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { TeamRole } from 'app/entities/enumerations/team-role.model';
import { GameService } from 'app/entities/game/service/game.service';
import { IGame } from 'app/entities/game/game.model';

@Component({
  selector: 'jhi-team-view',
  templateUrl: './team-view.component.html',
  styleUrls: ['./team-view.component.scss'],
})
export class TeamViewComponent implements OnInit {
  team: ITeam = { id: 404 };
  matches: Array<IMatch> = new Array<IMatch>();
  games: Array<IGame> = new Array<IGame>();
  members: Array<ITeamPlayer> = new Array<ITeamPlayer>();
  account: Account | null = null;
  playerData: IPlayerData | null = null;

  constructor(
    protected dataUtils: DataUtils,
    protected activatedRoute: ActivatedRoute,
    protected matchService: MatchService,
    protected teamPlayerService: TeamPlayerService,
    protected playerDataService: PlayerDataService,
    protected accountService: AccountService,
    protected teamService: TeamService,
    protected gameService: GameService,
    protected router: Router
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ team }) => {
      this.team = team;
      console.log(team);
      this.accountService.getAuthenticationState().subscribe(account => {
        this.account = account;
        if (this.account != null) {
          this.playerDataService.findPlayerDataByUserId((this.account as any).id).subscribe(playerData => {
            this.playerData = playerData.body as IPlayerData;
            console.log('player data');
            console.log(this.playerData);
          });
        } else {
          console.log('Account is null.');
        }
      });

      this.matchService.findAllByTeamId(this.team.id).subscribe({
        next: res => {
          console.log('matches');
          console.log(res);
          res.body?.map(match => {
            this.matches.push(match);
            this.gameService.getGamesByMatchId(match.id).subscribe({
              next: gameRes => {
                gameRes.body?.map(game => {
                  this.games.push(game);
                });
                console.log(this.games);
              },
            });
          });
        },
      });

      this.getAllMembers();
    });
  }

  isPublicTeam(): boolean {
    return this.team.accessStatus === AccessStatus.PUBLIC;
  }
  isPrivateTeam(): boolean {
    return this.team.accessStatus === AccessStatus.PRIVATE;
  }
  isClosedTeam(): boolean {
    return !this.isPrivateTeam() && !this.isPublicTeam();
  }

  previousState(): void {
    window.history.back();
  }

  getAllMembers(): void {
    this.teamPlayerService.getTeamPlayersByTeamId(this.team.id).subscribe({
      next: res => {
        console.log('init members');
        console.log(res);
        this.members = res;
        this.playerDataService.getPlayerDataByPlayerIds(this.members.map((member: ITeamPlayer) => member.player!.id)).subscribe({
          next: data => {
            data.forEach(playerData => {
              this.members.find(value => value.player!.id === playerData.id)!.player = playerData;
            });
            console.log('members with player data');
            console.log(this.members);
          },
        });
      },
    });
  }

  getMembers(filter: boolean): Array<ITeamPlayer> {
    const result = new Array<ITeamPlayer>();
    this.members.map(player => {
      if (player.accepted === filter) {
        result.push(player);
      }
    });
    return result;
  }
  getWins(): number {
    let count = 0;
    this.matches.forEach(match => {
      if (this.isWinner(match)) {
        count += 1;
      }
    });
    return count;
  }
  getLosses(): number {
    let count = 0;
    this.matches.forEach(match => {
      if (!this.isWinner(match)) {
        count += 1;
      }
    });
    return count;
  }
  isWinner(match: IMatch): boolean {
    const winner: ITeam = match.oneScore! > match.twoScore! ? match.teamOne! : match.teamTwo!;
    return winner.id === this.team.id;
  }
  isMember(): boolean {
    if (!this.account || !this.playerData) {
      return false;
    }
    return this.members.filter(teamPlayer => teamPlayer.player?.id === this.playerData?.id).length !== 0;
  }
  isAccepted(): boolean {
    if (!this.account || !this.playerData) {
      return false;
    }
    if (!this.isMember()) {
      return false;
    }
    return this.members.filter(teamPlayer => teamPlayer.player?.id === this.playerData?.id && teamPlayer.accepted).length !== 0;
  }

  isLeader(): boolean {
    if (!this.account || !this.playerData) {
      return false;
    }
    return (
      this.members.filter(teamPlayer => teamPlayer.role === TeamRole.LEADER && teamPlayer.player?.id === this.playerData?.id).length !== 0
    );
  }
  joinTeam(): void {
    if (this.account == null) {
      this.router.navigate(['login']);
    }
    this.teamService.join(this.team.id).subscribe({
      next: res => {
        console.log('join team result');
        console.log(res);
        this.getAllMembers();
      },
    });
  }
  getMapImage(match: IMatch): string {
    const g = this.games.filter((x: IGame) => x.match?.id === match.id)[0];
    if (g == null) {
      return '';
    }
    if (g.overwatchMap === undefined || g.overwatchMap == null) {
      return '';
    }
    const id = g.overwatchMap.id;
    const url = '../../../content/images/map' + id.toString() + '.png';
    console.log(url);
    return url;
  }
  leaveTeam(): void {
    if (this.account == null) {
      this.router.navigate(['login']);
    }

    this.teamService.leave(this.team.id).subscribe({
      next: res => {
        console.log('leave team result');
        console.log(res);
        this.getAllMembers();
      },
    });
  }
  acceptApplicant(id: number): void {
    if (!this.isLeader()) {
      return;
    }
    const applicant = this.getMembers(false).find(teamPlayer => teamPlayer.id === id);
    if (applicant) {
      applicant.accepted = true;
      this.teamPlayerService.update(applicant).subscribe({
        next: res => {
          console.log('accept result');
          console.log(res);
        },
      });
    }
  }
  rejectApplicant(id: number): void {
    if (!this.isLeader()) {
      return;
    }
    const applicant = this.getMembers(false).find(teamPlayer => teamPlayer.id === id);
    if (applicant) {
      this.teamPlayerService.delete(id).subscribe({
        next: res => {
          console.log('reject result');
          console.log(res);
        },
      });
    }
  }
}
