import { Component, OnInit } from '@angular/core';

import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { MatchService } from 'app/entities/match/service/match.service';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';
import { TeamPlayerService } from 'app/entities/team-player/service/team-player.service';
import { TeamService } from 'app/entities/team/service/team.service';
import { AccountService } from 'app/core/auth/account.service';
import { TournamentService } from 'app/entities/tournament/service/tournament.service';

import { MAT_SELECT_SCROLL_STRATEGY_PROVIDER } from '@angular/material/select';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { Account } from 'app/core/auth/account.model';

import { FormControl, FormGroup, Validators, ValidatorFn, AbstractControl } from '@angular/forms';
import { OverwatchMapService } from 'app/entities/overwatch-map/service/overwatch-map.service';
import { IGame, NewGame } from 'app/entities/game/game.model';
import { IMatch } from 'app/entities/match/match.model';
import { IOverwatchMap } from 'app/entities/overwatch-map/overwatch-map.model';
import { GameService } from 'app/entities/game/service/game.service';
import { GamePlayerService } from 'app/entities/game-player/service/game-player.service';
import { NewGamePlayer } from 'app/entities/game-player/game-player.model';
import { GameTeam } from 'app/entities/enumerations/game-team.model';
import dayjs from 'dayjs';

@Component({
  selector: 'jhi-match-submit',
  templateUrl: './match-submit.component.html',
  styleUrls: ['./match-submit.component.scss'],
  providers: [MAT_SELECT_SCROLL_STRATEGY_PROVIDER],
})
export class MatchSubmitComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private matchService: MatchService,
    private teamService: TeamService,
    private playerDataService: PlayerDataService,
    private overwatchMapService: OverwatchMapService,
    private tournamentService: TournamentService,
    private gameService: GameService,
    private gamePlayerService: GamePlayerService
  ) {}

  //Setup Player Select Lists
  teamOneSelections: string[][] = [['-1']];

  changedOne(i: number) {
    if (this.scoreSubmitFormControl.get('teamOnePlayers' + i)?.value.length < 6) {
      this.teamOneSelections[i] = this.scoreSubmitFormControl.get('teamOnePlayers' + i)?.value;
    } else {
      this.scoreSubmitFormControl.get('teamOnePlayers' + i)?.setValue(this.teamOneSelections[i]);
    }
    //console.log('Form Value: ', this.scoreSubmitFormControl.get('teamOnePlayers' + i)?.value);
    //console.log('Selection Value: ', this.teamOneSelections);
  }

  teamTwoSelections: string[][] = [['-1']];

  changedTwo(i: number) {
    if (this.scoreSubmitFormControl.get('teamTwoPlayers' + i)?.value.length < 6) {
      this.teamTwoSelections[i] = this.scoreSubmitFormControl.get('teamTwoPlayers' + i)?.value;
    } else {
      this.scoreSubmitFormControl.get('teamTwoPlayers' + i)?.setValue(this.teamTwoSelections[i]);
    }
    //console.log(this.scoreSubmitFormControl.get('teamOnePlayers' + i)?.value);
    //console.log(this.teamTwoSelections);
  }

  //matchId from url
  matchId: number = -1;

  //Data
  matchData: any;
  tournamentData: any;

  //map number from tournamentData
  mapNum: number = -1;

  //Account
  account: Account | null = null;

  //account playerData
  accountPD: any;

  //booleans for perms
  admin: any;
  submitPerm: boolean = false;

  //Teams Data
  teamOneData: any;
  teamOnePlayers: any[] = [];
  teamOneScore: number = 0;

  teamTwoData: any;
  teamTwoPlayers: any[] = [];
  teamTwoScore: number = 0;

  //All maps
  maps: any;

  //New game object
  game: NewGame = {
    id: null,
    order: null,
    scoreOne: null,
    scoreTwo: null,
    overwatchMap: null,
    match: null,
    replay: null,
  };

  //foreign objects for game
  matchIdObject!: Pick<IMatch, 'id'>;
  overwatchMapIdObject!: Pick<IOverwatchMap, 'id'>;

  //New GamePlayer object
  gamePlayer: NewGamePlayer = {
    id: null,
    team: null,
    game: null,
    playerData: null,
  };

  //Match Update Object
  matchUpdate: IMatch = {
    id: -1,
    matchIndex: null,
    oneScore: null,
    twoScore: null,
    startTime: null,
    endTime: null,
    scoreSubmitted: true,
    scoreApproved: null,
    tournament: null,
    teamOne: null,
    teamTwo: null,
  };

  //Player Update Object
  playerUpdate: IPlayerData = {
    id: -1,
    name: null,
    overwatchUsername: null,
    profile: null,
    profileContentType: null,
    bio: null,
    primaryLanguage: null,
    device: null,
    matchesPlayed: null,
    tournamentsPlayed: null,
    matchWins: null,
    tournamentWins: null,
    tournamentTop10s: null,
    user: null,
    base64Profile: null,
    teamRole: null,
  };

  //foreign objects for GamePlayer
  playerDataIdObject!: Pick<IPlayerData, 'id'>;
  gameIdObject!: Pick<IGame, 'id'>;

  //temp values for form
  errorMsgs: string[] = [];
  currentmsg: string = '';
  currentMapGood: boolean = false;
  validForm: boolean = true;

  //get range for loops
  getRange(total: number): number[] {
    return Array.from({ length: total }, (_, index) => index + 1);
  }

  playerListValidator(i: number): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const playerCount = control.value ? control.value.length : 0;

      const mapValue = this.scoreSubmitFormControl.get('map' + i)?.value;
      if (mapValue === 'no-play') {
        return null;
      }

      if (playerCount !== 5) {
        console.log('INVALID');
        return { playerList: { valid: false, message: 'Player Count Validation Failed' } };
      } else {
        console.log('VALID');
        return null;
      }
    };
  }

  scoreValidator(i: number): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const score = control.value;

      const mapValue = this.scoreSubmitFormControl.get('map' + i)?.value;
      if (mapValue === 'no-play') {
        return null;
      }

      if (score == null) {
        console.log('INVALID');
        return { score: { valid: false, message: 'Score Validation Failed' } };
      } else {
        console.log('VALID');
        return null;
      }
    };
  }

  //Setup dynamic Form
  initializeFormControls(mapNum: number): FormGroup {
    const formControls: { [key: string]: FormControl } = {};

    for (let i = 1; i <= mapNum; i++) {
      this.teamOneSelections.push(['-1']);
      this.teamTwoSelections.push(['-1']);

      // , { validators: [Validators.required] }
      formControls['map' + i] = new FormControl(null, { validators: [Validators.required] });
      formControls['replay' + i] = new FormControl(null);
      formControls['endTime' + i] = new FormControl(null);

      formControls['teamOneScore' + i] = new FormControl(null, { validators: [this.scoreValidator(i)] });
      formControls['teamTwoScore' + i] = new FormControl(null, { validators: [this.scoreValidator(i)] });

      //, { validators: [this.playerListValidator(i)] }
      formControls['teamOnePlayers' + i] = new FormControl(null, { validators: [this.playerListValidator(i)] });
      formControls['teamTwoPlayers' + i] = new FormControl(null, { validators: [this.playerListValidator(i)] });
    }

    return new FormGroup(formControls);
  }

  //Create FormGroup variable
  scoreSubmitFormControl: FormGroup = new FormGroup({});

  canSubmitResults(): boolean {
    this.validForm = true;
    this.errorMsgs = [];

    //for each game
    for (let i = 1; i <= this.mapNum; i++) {
      //reset temp values
      this.currentMapGood = true;
      this.currentmsg = '';
      this.currentmsg = this.currentmsg + `Map ${i}: `;

      // check all maps are not null
      if (this.scoreSubmitFormControl.get('map' + i)?.value == null) {
        this.validForm = false;
        this.currentmsg = this.currentmsg + `   Map not Selected,   `;
        this.currentMapGood = false;
      }

      //Check for TeamOne Score
      if (
        this.scoreSubmitFormControl.get('map' + i)?.value != 'no-play' &&
        this.scoreSubmitFormControl.get('teamOneScore' + i)?.value == null
      ) {
        this.validForm = false;
        this.currentmsg = this.currentmsg + `   Missing Team 1 Score,  `;
        this.currentMapGood = false;
      }

      //Check For TeamOne Players
      if (this.scoreSubmitFormControl.get('map' + i)?.value != 'no-play' && this.teamOneSelections[i]?.length < 5) {
        this.validForm = false;
        this.currentmsg = this.currentmsg + `   Missing Team 1 Players,   `;
        this.currentMapGood = false;
      }

      //Check For TeamTwo Score
      if (
        this.scoreSubmitFormControl.get('map' + i)?.value != 'no-play' &&
        this.scoreSubmitFormControl.get('teamTwoScore' + i)?.value == null
      ) {
        this.validForm = false;
        this.currentmsg = this.currentmsg + `   Missing Team 2 Score,  `;
        this.currentMapGood = false;
      }

      //Check for TeamTwo Players
      if (this.scoreSubmitFormControl.get('map' + i)?.value != 'no-play' && this.teamTwoSelections[i]?.length < 5) {
        this.validForm = false;
        this.currentmsg = this.currentmsg + `   Missing Team 2 Players,  `;
        this.currentMapGood = false;
      }

      if (!this.currentMapGood) {
        this.errorMsgs.push(this.currentmsg);
      }
    }

    //console.log('FORM VALID: ',this.scoreSubmitFormControl.valid);
    //console.log('VALID: ', this.validForm);
    return this.validForm && this.scoreSubmitFormControl.valid;
  }

  updatePlayerData(id: number, win: boolean) {
    this.playerDataService.find(id).subscribe({
      next: res => {
        this.playerUpdate.id = id;
        this.playerUpdate.matchesPlayed = res.body!.matchesPlayed! + 1;
        if (win) {
          this.playerUpdate.matchWins = res.body!.matchWins! + 1;
        } else {
          this.playerUpdate.matchWins = null;
        }

        this.playerDataService.partialUpdate(this.playerUpdate).subscribe({
          next: res => {},
          error: error => {
            console.error('Error Updating PlayerData', error);
          },
        });
      },
      error: error => {
        console.error('Error Finding PlayerData', error);
      },
    });
  }

  finishScoreSubmit(): void {
    //console.log('DONE');

    this.matchUpdate.id = this.matchId;

    //Set one / two score
    this.matchUpdate.oneScore = this.teamOneScore;
    this.matchUpdate.twoScore = this.teamTwoScore;

    //Set End Time
    this.matchUpdate.endTime = this.scoreSubmitFormControl.get('endtime')?.value;

    //this.matchUpdate.endTime = this.timetest;

    this.matchService.partialUpdate(this.matchUpdate).subscribe({
      next: response => {},
      error: error => {
        console.error('Error setting submit to true:', error);
      },
    });

    this.router.navigate(['match-view', this.matchId]);
  }

  saveFormResults(): void {
    if (this.canSubmitResults()) {
      //console.log(this.scoreSubmitFormControl.getRawValue())

      for (let i = 1; i <= this.mapNum; i++) {
        if (this.scoreSubmitFormControl.get('map' + i)?.value != 'no-play') {
          this.matchIdObject = { id: this.matchId };
          this.game.match = this.matchIdObject;

          this.overwatchMapIdObject = { id: this.scoreSubmitFormControl.get('map' + i)?.value };
          this.game.overwatchMap = this.overwatchMapIdObject;

          this.game.order = i;
          this.game.scoreOne = this.scoreSubmitFormControl.get('teamOneScore' + i)?.value;
          this.game.scoreTwo = this.scoreSubmitFormControl.get('teamTwoScore' + i)?.value;
          this.game.replay = this.scoreSubmitFormControl.get('replay' + i)?.value;

          // Calc Total Score
          if (this.scoreSubmitFormControl.get('teamOneScore' + i)?.value > this.scoreSubmitFormControl.get('teamTwoScore' + i)?.value) {
            this.teamOneScore += 1;
          } else {
            this.teamTwoScore += 1;
          }

          //console.log('GameDTO: ', this.game);
          this.gameService.create(this.game);

          this.gameService.create(this.game).subscribe({
            next: res => {
              //console.log('Response:', res.body?.id);

              //set gameID
              this.gameIdObject = { id: res.body!.id };
              this.gamePlayer.game = this.gameIdObject;

              //set team 1
              this.gamePlayer.team = GameTeam.TEAM_ONE;

              for (let playerId of this.teamOneSelections[i]) {
                //set playerDataId
                this.playerDataIdObject = { id: +playerId };
                this.gamePlayer.playerData = this.playerDataIdObject;
                this.gamePlayerService.create(this.gamePlayer).subscribe({
                  next: res => {
                    console.log('Create gamePlayer Response:', res);
                  },
                  error: error => {
                    console.error('Error creating teamOne Gameplayer:', error);
                  },
                });
                this.updatePlayerData(+playerId, this.teamOneScore > this.teamTwoScore);
              }

              //set team 2
              this.gamePlayer.team = GameTeam.TEAM_TWO;

              for (let playerId of this.teamTwoSelections[i]) {
                //set playerDataId
                this.playerDataIdObject = { id: +playerId };
                this.gamePlayer.playerData = this.playerDataIdObject;
                this.gamePlayerService.create(this.gamePlayer).subscribe({
                  next: res => {
                    console.log('Create gamePlayer Response:', res);
                  },
                  error: error => {
                    console.error('Error creating teamOne Gameplayer:', error);
                  },
                });
                this.updatePlayerData(+playerId, this.teamOneScore < this.teamTwoScore);
              }
            },
            error: error => {
              console.error('Error:', error);
            },
          });
        }
      }
      this.finishScoreSubmit();
    } else {
      console.log('SUBMIT FAIL');
      this.scoreSubmitFormControl.markAllAsTouched();
    }
  }

  ngOnInit(): void {
    console.log(dayjs());
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
      } else {
        console.log('Account is null.');
        this.router.navigate(['404']);
      }
    });

    //Get Maps
    this.overwatchMapService.query().subscribe({
      next: res => {
        this.maps = res.body;
        this.maps = this.maps.sort((a: any, b: any) => a.name.localeCompare(b.name));
        this.maps = this.maps.sort((a: any, b: any) => a.mode.localeCompare(b.mode));
        //console.log('MAPS: ', this.maps);
      },
      error: error => {
        console.error('MAPS Error:', error);
      },
    });

    //Get Data For The Match
    this.matchService.find(this.matchId).subscribe({
      next: res => {
        this.matchData = res;
        //console.log('matchData:', this.matchData);

        this.tournamentService.find(this.matchData.body.tournament.id).subscribe({
          next: res => {
            this.tournamentData = res;
            //console.log('Tournament: ', this.tournamentData);

            this.mapNum = +this.tournamentData.body.gamesPerMatch;

            this.scoreSubmitFormControl = this.initializeFormControls(this.mapNum);

            //make map update player list validation
            for (let i = 1; i <= this.mapNum; i++) {
              this.scoreSubmitFormControl.get('map' + i)?.valueChanges.subscribe(() => {
                this.scoreSubmitFormControl.get('teamOnePlayers' + i)?.updateValueAndValidity();
                this.scoreSubmitFormControl.get('teamTwoPlayers' + i)?.updateValueAndValidity();
                this.scoreSubmitFormControl.get('teamOneScore' + i)?.updateValueAndValidity();
                this.scoreSubmitFormControl.get('teamTwoScore' + i)?.updateValueAndValidity();
              });
            }
          },
          error: error => {
            console.error('Error fetching tournamentData:', error);
          },
        });

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
          },
          error: error => {
            console.error('Error fetching teamOnePlayers:', error);
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

        //If User doesnt have submit perms and isn't admin
        if ((!this.submitPerm && !this.admin) || this.matchData?.body.scoreSubmitted == true) {
          this.router.navigate(['404']);
        }

        //Get TeamOne Data
        this.teamService.find(this.matchData.body.teamOne.id).subscribe({
          next: res => {
            this.teamOneData = res;
            //console.log(this.teamOneData);
          },
          error: error => {
            console.error('Error fetching teamOne:', error);
          },
        });

        //Get TeamTwo
        this.teamService.find(this.matchData.body.teamTwo.id).subscribe({
          next: res => {
            this.teamTwoData = res;
            //console.log(this.teamTwoData);
          },
          error: error => {
            console.error('Error fetching teamTwo:', error);
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
