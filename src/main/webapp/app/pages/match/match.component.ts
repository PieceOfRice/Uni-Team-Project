import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatchService } from 'app/entities/match/service/match.service';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';
import { TeamPlayerService } from 'app/entities/team-player/service/team-player.service';
import { TeamService } from 'app/entities/team/service/team.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { TournamentService } from 'app/entities/tournament/service/tournament.service';
import { GameService } from 'app/entities/game/service/game.service';
import { OverwatchMapService } from 'app/entities/overwatch-map/service/overwatch-map.service';
import { GamePlayerService } from 'app/entities/game-player/service/game-player.service';
import { Router } from '@angular/router';
import { IMatch } from 'app/entities/match/match.model';

@Component({
  selector: 'jhi-match',
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.scss'],
})
export class MatchComponent implements OnInit {
  mapimageurls: string[] = [];

  imageurlstart: string = '../../../content/images/map';
  imageurlend: string = '.png';

  matchId: string = '';
  matchIdNum: number = -1;
  matchData: any;
  tournamentData: any;

  teamOneData: any;
  teamOnePlayers: any[] = [];

  teamTwoData: any;
  teamTwoPlayers: any[] = [];

  gamesData: any;
  maps: any[] = [];

  allGamePlayers: any;
  tempOneGamePlayers: any[] = [];
  tempTwoGamePlayers: any[] = [];
  teamOneGamePlayers: any[][] = [];
  teamTwoGamePlayers: any[][] = [];

  teamOneWins: number = 0;
  teamTwoWins: number = 0;
  teamOneWin: boolean = true;

  account: Account | null = null;
  accountPD: any;
  submitPerm: boolean = false;
  approvePerm: boolean = false;

  admin: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private matchService: MatchService,
    private teamService: TeamService,
    private playerDataService: PlayerDataService,
    private accountService: AccountService,
    private tournamentService: TournamentService,
    private gameService: GameService,
    private mapService: OverwatchMapService,
    private gamePlayerService: GamePlayerService,
    private router: Router
  ) {}

  //Match Update Object
  matchUpdate: IMatch = {
    id: -1,
    matchIndex: null,
    oneScore: null,
    twoScore: null,
    startTime: null,
    endTime: null,
    scoreSubmitted: null,
    scoreApproved: null,
    tournament: null,
    teamOne: null,
    teamTwo: null,
  };

  approveScore(): void {
    this.matchUpdate.id = this.matchIdNum;
    this.matchUpdate.scoreApproved = true;

    this.matchService.partialUpdate(this.matchUpdate).subscribe({
      next: response => {},
      error: error => {
        console.error('Error setting Approved to true:', error);
      },
    });

    this.matchUpdate.scoreApproved = null;
    window.location.reload();
  }

  async deleteGames(): Promise<void> {
    console.log('Deleting Games');

    for (let game of this.gamesData.body) {
      console.log('DELETING GAME: ', game.id);
      try {
        await this.gamePlayerService.deleteByGameId(game.id).toPromise();
        console.log('Game players deleted successfully');

        await this.gameService.delete(game.id).toPromise();
        console.log('Game deleted successfully');
      } catch (error) {
        console.error('Error deleting game:', error);
        // Handle errors if needed
      }
    }

    console.log('All delete operations completed');
    window.location.reload();
  }

  unSubmit(): void {
    this.matchUpdate.id = this.matchIdNum;
    this.matchUpdate.scoreSubmitted = false;

    this.matchService.partialUpdate(this.matchUpdate).subscribe({
      next: response => {},
      error: error => {
        console.error('Error setting Submitted to false:', error);
      },
    });

    this.matchUpdate.scoreApproved = null;

    this.deleteGames();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.matchId = params['id'];
    });

    //Get Current User Accoount + Their PlayerData
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;

      if (this.account != null) {
        //get PlayerData of User
        this.playerDataService.findPlayerDataByUserId((this.account as any).id).subscribe(playerData => {
          this.accountPD = playerData;
        });

        //Check if Admin
        this.admin = this.accountService.hasAnyAuthority('ROLE_ADMIN');
        if (this.admin) {
          this.submitPerm = true;
          this.approvePerm = true;
        }
      } else {
        console.log('Account is null.');
      }
    });

    this.matchIdNum = +this.matchId;

    //Get Data For The Match
    this.matchService.find(this.matchIdNum).subscribe({
      next: res => {
        this.matchData = res;
        console.log('matchData:', this.matchData);

        this.tournamentService.find(this.matchData.body.tournament.id).subscribe({
          next: res => {
            this.tournamentData = res;
            console.log('TournamentData: ', res);

            if (this.tournamentData?.body.creator.id == this.accountPD.body.id) {
              this.approvePerm = true;
              this.submitPerm = true;
            }
            console.log('APPROVE PERM: ', this.approvePerm, this.accountPD);
          },
          error: error => {
            console.error('Error fetching tournamentData:', error);
          },
        });

        //Get Game Data
        if (this.matchData.body.scoreSubmitted) {
          this.gameService.getGamesByMatchId(this.matchIdNum).subscribe({
            next: res => {
              this.gamesData = res;
              console.log('Games Data:', this.gamesData);

              //loop through each map
              for (let game of this.gamesData.body) {
                console.log('GAME: ', game);
                console.log('SCOREONE: ', game.scoreOne);
                console.log('SCORETWO: ', game.scoreTwo);
                //work out winner
                if (game.scoreOne > game.scoreTwo) {
                  this.teamOneWins++;
                  console.log('T1W: ', this.teamOneWins);
                }

                if (game.scoreOne < game.scoreTwo) {
                  this.teamTwoWins++;
                  console.log('T2W: ', this.teamTwoWins);
                }

                game.mapimage = this.imageurlstart + game.overwatchMap.id + this.imageurlend;
                console.log('Games Data:', this.gamesData);

                //Get Map Names
                this.mapService.find(game.overwatchMap.id).subscribe({
                  next: res => {
                    this.maps.push(res);
                    game.mapName = res.body?.name;
                  },
                });

                this.gamePlayerService.getGamePlayersByGameId(game.id).subscribe({
                  next: res => {
                    this.allGamePlayers = res;
                    //console.log('API RESPONSE: ', this.allGamePlayers);

                    for (let gamePlayer of this.allGamePlayers.body) {
                      //console.log('GAMEPLAYER: ', gamePlayer);

                      this.playerDataService.find(gamePlayer.playerData.id).subscribe({
                        next: res => {
                          gamePlayer.playerData = res;
                        },
                      });

                      if (gamePlayer.team == 'TEAM_ONE') {
                        this.tempOneGamePlayers.push(gamePlayer);
                      }

                      if (gamePlayer.team == 'TEAM_TWO') {
                        this.tempTwoGamePlayers.push(gamePlayer);
                      }
                    }

                    this.teamOneGamePlayers.push(this.tempOneGamePlayers);
                    this.teamTwoGamePlayers.push(this.tempTwoGamePlayers);
                    game.teamOnePlayers = this.tempOneGamePlayers;
                    game.teamTwoPlayers = this.tempTwoGamePlayers;

                    this.tempOneGamePlayers = [];
                    this.tempTwoGamePlayers = [];

                    //console.log('tempOneGamePlayers: ',this.tempOneGamePlayers)
                    //console.log('tempTwoGamePlayers: ',this.tempTwoGamePlayers)
                  },
                });
              }

              //console.log('Maps: ', this.maps);
              //console.log('TeamOne GamePlayers', this.teamOneGamePlayers);
              //console.log('TeamTwo GamePlayers', this.teamTwoGamePlayers);
            },
          });
        }

        //Get TeamOne Data
        this.teamService.find(this.matchData.body.teamOne.id).subscribe({
          next: res => {
            this.teamOneData = res;
          },
          error: error => {
            console.error('Error fetching teamOne:', error);
          },
        });

        //get TeamOne Players
        this.playerDataService.findAllPlayerDataByTeamId(this.matchData.body.teamOne.id).subscribe({
          next: res => {
            this.teamOnePlayers = res.body!;
            console.log('TEAM ONE: ', this.teamOnePlayers);

            if (this.account) {
              for (let teamPlayer of this.teamOnePlayers) {
                if (this.accountPD.body.id == teamPlayer[0].id && teamPlayer[1] == 'LEADER') {
                  this.submitPerm = true;
                }
              }
            }
          },
          error: error => {
            console.error('Error fetching teamOnePlayers:', error);
          },
        });

        //Get TeamTwo Data
        this.teamService.find(this.matchData.body.teamTwo.id).subscribe({
          next: res => {
            this.teamTwoData = res;

            console.log('TEAM TWO: ', this.teamTwoData);

            if (this.account) {
              for (let teamPlayer of this.teamOnePlayers) {
                if (this.accountPD.body.id == teamPlayer[0].id && teamPlayer[1] == 'LEADER') {
                  this.submitPerm = true;
                }
              }
            }
          },
          error: error => {
            console.error('Error fetching teamTwo:', error);
          },
        });

        //get TeamTwo Players
        this.playerDataService.findAllPlayerDataByTeamId(this.matchData.body.teamTwo.id).subscribe({
          next: res => {
            this.teamTwoPlayers = res.body!;
            console.log('TEAM TWO: ', this.teamTwoPlayers);
          },
          error: error => {
            console.error('Error fetching teamTwoPlayers:', error);
          },
        });
      },
      error: error => {
        console.error('Error fetching match:', error);
        this.router.navigate(['404']);
      },
    });
  }
}
