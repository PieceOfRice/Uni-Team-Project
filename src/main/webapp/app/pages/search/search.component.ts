import { Component, OnInit } from '@angular/core';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { MAT_SELECT_SCROLL_STRATEGY_PROVIDER, MatSelectChange, MatSelectConfig } from '@angular/material/select';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';
import { TeamPlayerService } from 'app/entities/team-player/service/team-player.service';
import { TournamentService } from 'app/entities/tournament/service/tournament.service';
import { ITeamPlayer } from 'app/entities/team-player/team-player.model';
import { TeamService } from 'app/entities/team/service/team.service';
import { ITeam } from 'app/entities/team/team.model';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { MatOptionSelectionChange } from '@angular/material/core';
import { IMatch } from 'app/entities/match/match.model';
import { MatchService } from 'app/entities/match/service/match.service';

@Component({
  selector: 'jhi-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss'],
  providers: [MAT_SELECT_SCROLL_STRATEGY_PROVIDER, { provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: { appearance: 'outline' } }],
})
export class SearchComponent implements OnInit {
  inputValue: string;
  username: string;
  playerService: PlayerDataService;
  teamPlayerService: TeamPlayerService;
  teamService: TeamService;
  playerData: IPlayerData | null;
  bio: string;
  team1: string;
  teamPlayerData: ITeamPlayer | null;
  playerDataArray: IPlayerData[];

  team: ITeam | null;
  teamArray: ITeam[];

  tournament: ITournament | null;
  tournamentArray: ITournament[];
  tournamentService: TournamentService;
  inputType: string;

  playerTeams: ITeam[][];

  tournamentMatches: IMatch[];

  tournamentMatchTeams: ITeam[][];
  // null for every one that isnt a match

  tournamentStandings: ITeam[][];
  // null for every one that isnt a team

  tmp_tournamentStandings: ITeam[][];

  tournamentMatchIndex: number;

  tournamentStandingIndex: number;

  matchService: MatchService;

  new_team_list: ITeam[];

  tmp_teams: ITeam[];

  constructor(
    playerService: PlayerDataService,
    teamPlayerService: TeamPlayerService,
    matchService: MatchService,
    teamService: TeamService,
    tournamentService: TournamentService
  ) {
    this.inputValue = '';
    this.username = 'default';
    this.playerService = playerService;
    this.teamPlayerService = teamPlayerService;
    this.matchService = matchService;
    this.playerData = null;
    this.bio = '';
    this.team1 = '';
    this.teamPlayerData = null;
    this.playerDataArray = [];

    this.teamService = teamService;
    this.teamArray = [];
    this.team = null;

    this.tournamentService = tournamentService;
    this.tournamentArray = [];
    this.tournament = null;

    this.playerTeams = [];
    this.inputType = 'all';
    this.tournamentMatches = [];
    this.tournamentStandings = [];

    this.tournamentMatchIndex = 0;
    this.tournamentStandingIndex = 0;
    this.tmp_tournamentStandings = [];
    this.tournamentMatchTeams = [];
    this.new_team_list = [];
    this.tmp_teams = [];
  }

  ngOnInit(): void {}

  updateSearch() {
    this.playerDataArray = [];
    this.teamArray = [];
    this.tournamentArray = [];
    this.playerTeams = [];

    if (this.inputType == 'all' || this.inputType == 'players') {
      this.updatePlayerSearch();
    }
    if (this.inputType == 'all' || this.inputType == 'teams') {
      this.updateTeamSearch();
    }
    if (this.inputType == 'all' || this.inputType == 'tournaments') {
      this.updateTournamentSearch();
    }
  }

  updatePlayerSearch() {
    this.playerService.findPlayerDataByNameLike(this.inputValue).subscribe({
      next: value => {
        //console.log(value);
        //console.log('body: ', value!.body![0]);

        for (let i = 0; i < value!.body!.length; i++) {
          this.playerData = value!.body![i] as IPlayerData;
          //console.log('Player Data:', this.playerData);

          this.playerDataArray.push(this.playerData);

          //console.log(value);

          this.teamService.getTeamDataByPlayerId(this.playerData.id).subscribe({
            next: value => {
              if (value.body) {
                // let teams = value.body as Array<ITeam>;
                // this.team1 = teams[0].name as string;

                let tempteams = value.body;
                this.playerTeams.push(tempteams);

                console.log('Player Teams:', this.playerTeams);
              }
            },
          });
        }
        console.log('Player Array: ', this.playerDataArray);
      },
      error: err => {
        console.log(err);
        // when not found gives error
      },
      complete: () => console.log('completed'),
    });
  }

  updateTeamSearch() {
    this.teamService.findTeamByNameLike(this.inputValue).subscribe({
      next: value => {
        for (let i = 0; i < value!.body!.length; i++) {
          this.team = value!.body![i] as ITeam;
          //console.log('Team:', this.team);

          this.teamArray!.push(this.team);
        }
        console.log('Team Array: ', this.teamArray);
      },
      error: err => {
        console.log(err);
        // when not found gives error
      },
      complete: () => console.log('completed'),
    });
  }

  // updateTournamentStandingTeamData() {
  //   for (let i=0;i<this.tmp_tournamentStandings.length;i++) {
  //     let tmp_teams: ITeam[] = [];
  //     for (let y=0;y<this.tmp_tournamentStandings[i].length;y++) {
  //       this.teamService.find(this.tmp_tournamentStandings[i][y].id).subscribe({
  //         next: value => {
  //             tmp_teams.push(value.body as ITeam);
  //         },
  //         error: err => {
  //           console.log(err);
  //         }
  //       })
  //     }
  //     this.tournamentStandings.push(tmp_teams);
  //   }
  // }

  updateTournamentSearch() {
    this.tournamentService.findTournamentByNameLike(this.inputValue).subscribe({
      next: value => {
        for (let i = 0; i < value!.body!.length; i++) {
          this.tournament = value!.body![i] as ITournament;
          //console.log('Team:', this.team);

          this.tournamentArray.push(this.tournament);

          console.log(this.tournament.ended);

          if (this.tournament.ended) {
            this.tournamentMatches[i] = null as any;
            this.new_team_list = [];
            // this.tournamentMatches.push(null as any);

            this.matchService.findFinalThreeMatchesByTournamentId(this.tournament.id).subscribe({
              next: value => {
                let match_list = value!.body! as IMatch[];
                console.log('match list before slice', match_list);
                if (match_list.length > 3) {
                  match_list = match_list.slice(0, 3);
                  console.log('SLICED');
                }

                // match_list = match_list.slice(0, 3);

                console.log(`tournament id: ${this.tournament!.id}`);
                console.log('match list:');
                console.log(match_list);

                if (match_list.length != 0) {
                  let team_list_tmp: ITeam[] = this.calculateStandings(match_list);

                  console.log('after calculate standings', team_list_tmp);
                  // if match_list != [] {
                  //   then do 3 null else null
                  // }

                  for (i = 0; i < team_list_tmp.length; i++) {
                    this.teamService.find(team_list_tmp[i].id).subscribe({
                      next: value => {
                        let teamtmp: ITeam = value.body as ITeam;

                        for (let x = 0; x < team_list_tmp.length; x++) {
                          this.new_team_list[team_list_tmp.findIndex(value => value.id === teamtmp.id)] = teamtmp;
                        }
                      },
                      error: err => {
                        console.log(err);
                      },
                    });
                  }
                  console.log('old team list:');
                  console.log(team_list_tmp);
                  console.log('new team list:');
                  console.log(this.new_team_list);
                  console.log('tournament standings array:');
                  console.log(this.tournamentStandings);
                }
              },
            });
            this.tournamentStandings[i] = this.new_team_list;
          } else {
            this.tournamentStandings[i] = null as any;
            // this.tournamentStandings.push(null as any);
            this.matchService.findFinalThreeMatchesByTournamentId(this.tournament.id).subscribe({
              next: value => {
                if (value.body!.length > 0) {
                  let tmp_match: IMatch = value.body![0] as IMatch;

                  this.tournamentMatches[i] = tmp_match;

                  let tmp_teams: ITeam[] = [];

                  this.teamService.find(tmp_match.teamOne!.id).subscribe({
                    next: value => {
                      tmp_teams[0] = value.body as ITeam;
                    },
                    error: err => {
                      console.log(err);
                    },
                    complete: () => {},
                  });

                  this.teamService.find(tmp_match.teamTwo!.id).subscribe({
                    next: value => {
                      tmp_teams[1] = value.body as ITeam;
                    },
                    error: err => {
                      console.log(err);
                    },
                    complete: () => {},
                  });
                  console.log('tournamentmatchesteams:');
                  console.log(this.tournamentMatchTeams);

                  this.tournamentMatchTeams[i] = tmp_teams;
                }
              },
            });

            console.log('tournamentMatches', this.tournamentMatches);
            // this.tournamentMatchTeams.push(this.tmp_teams);
          }
        }
        console.log('Tournament Array: ', this.tournamentArray);
      },
      error: err => {
        console.log(err);
        // when not found gives error
      },
      complete: () => {
        console.log('complete');
      },
    });
    // console.log(`length of tournament array: ${this.tournamentArray.length}`)
    // for (let i=0;i<this.tournamentArray.length;i++) {
    //   if (this.tournamentArray[i].ended) {
    //     this.calculateStandings(this.tournamentArray[i].id);
    //   }
    // }
  }

  // handling
  // showing multiple

  getStandings(tournamentId: number) {}

  calculateStandings(match_list: IMatch[]): ITeam[] {
    // given a list of matches, gives the teams in order 1st 2nd 3rd depending on number of matches
    let team_list: ITeam[] = [];

    if (match_list.length >= 1) {
      if (match_list[0].oneScore! > match_list[0].twoScore!) {
        team_list.push(match_list[0].teamOne!);
        // if match_list length = 1, return the 2 teams in the winning order
        team_list.push(match_list[0].teamTwo!);
      } else {
        team_list.push(match_list[0].teamTwo!);
        team_list.push(match_list[0].teamOne!);
      }
    }
    // if match list length=2 return 3rd team by checking the team that isn't 1st or 2nd
    if (match_list.length == 2) {
      // console.log("calculate standings debugging:");
      // console.log(match_list[1].teamOne!, match_list[1].teamTwo!);
      // console.log(!team_list.some((team) => {team.id === match_list[1].teamTwo!.id || team.id === match_list[1].teamOne!.id}));
      // console.log(team_list);
      if (
        !team_list.some(team => {
          team.id == match_list[1].teamOne!.id;
        })
      ) {
        team_list.push(match_list[1].teamOne!);
        console.log('team 1');
      } else if (
        !team_list.some(team => {
          team.id == match_list[1].teamTwo!.id;
        })
      ) {
        team_list.push(match_list[1].teamTwo!);
        console.log('team 2');
      }
      // if ([team_list[1].id, team_list[0].id].indexOf(match_list[1].teamOne!.id) == -1) {
      //   team_list.push(match_list[1].teamOne!);
      // } else {
      //   console.log("team 2 found")
      //   team_list.push(match_list[1].teamTwo!);
      // }
    }
    if (match_list.length == 3) {
      let scores_list: number[] = [];
      let ordered_teams: ITeam[] = [];
      let high_score: number = -1;
      let highest_team: ITeam = null as any;

      for (let i = 1; i < match_list.length; i++) {
        if (!team_list.some(team => team.id == match_list[i].teamOne!.id)) {
          ordered_teams.push(match_list[i].teamOne!);
          scores_list.push(match_list[i].oneScore!);
        }
        if (!team_list.some(team => team.id == match_list[i].teamTwo!.id)) {
          ordered_teams.push(match_list[i].teamTwo!);
          scores_list.push(match_list[i].twoScore!);
        }
      }

      for (let i = 0; i < scores_list.length; i++) {
        if (scores_list[i] > high_score) {
          high_score = scores_list[i];
          highest_team = ordered_teams[i];
        }
      }

      team_list.push(highest_team);
    }

    // if ([team_list[1].id, team_list[0].id].indexOf(match_list[1].teamOne!.id) == -1) {
    //   team_list.push(match_list[1].teamOne!);
    // } else {
    //   console.log("team 2 found")
    //   team_list.push(match_list[1].teamTwo!);

    // team_list.push(null as any);
    // let ordered_teams: ITeam[] = [];
    // let scores: Number[] = [];

    // // let ordered_list: number[] = [];

    // for (let i = 1; i < match_list.length; i++) {
    //   let match: IMatch = match_list[i] as IMatch;
    //   if (team_list.indexOf(match_list[i].teamOne!) == -1) {
    //     ordered_teams.push(match_list[i].teamOne!);
    //     scores.push(match_list[i].oneScore!);
    //   }
    //   if (team_list.indexOf(match_list[i].teamTwo!) == -1) {
    //     ordered_teams.push(match_list[i].teamTwo!);
    //     scores.push(match_list[i].twoScore!);
    //   }

    //   if (scores.length > 1) {
    //     if (scores[0] > scores[1]) {
    //       ordered_teams.pop();
    //     }
    //     if (scores[1] > scores[0]) {
    //       ordered_teams[0] = ordered_teams[1];
    //     }
    //   }

    //   team_list[2] = ordered_teams[0];

    // let high_score: number = 0;

    // // if match list length=3 return the 3rd team by comparing non-winning teams in match 2 and 3
    // // highest scorer = 3rd

    // console.log(`match type: ${typeof match}`);
    // if (!(match.teamOne!.id == team_list[0].id || match.teamOne!.id == team_list[1].id)) {
    //   if (match.oneScore! >= high_score) {
    //     team_list[2] = match.teamOne!;
    //     high_score = match.oneScore!;
    //   }
    // } else if (!(match.teamTwo!.id == team_list[0].id || match.teamTwo!.id == team_list[1].id)) {
    //   if (match.twoScore! >= high_score) {
    //     team_list[2] = match.teamTwo!;
    //     high_score = match.twoScore!;
    //   }
    // }

    // if (ordered_list[0] > ordered_list[1]) {
    //   if (!match_list.includes(match_list[1].teamOne!)) {
    //     team_list.push(match_list[1].teamOne!);
    //   } else if (!match_list.includes(match_list[1].teamTwo!)) {
    //     team_list.push(match_list[1].teamTwo!);
    //   }
    // } else {
    //   if (!match_list.includes(match_list[2].teamOne!)) {
    //     team_list.push(match_list[1].teamOne!);
    //   } else if (!match_list.includes(match_list[2].teamTwo!)) {
    //     team_list.push(match_list[1].teamTwo!);
    //   }
    // }

    return team_list!;

    // match_list.forEach((match) => {
    //   tmp_team_list.push(match.teamOne!);
    //   tmp_team_list.push(match.teamTwo!);
    // })

    // tmp_team_list.forEach((team) => {
    //   if (team_list.includes(team)) {
    //     tmp_team_list.splice(tmp_team_list.indexOf(team), 1);
    //   }
    // })
  }

  updateInput(e: any) {
    this.inputValue = e.target.value;
  }

  updateType() {
    if (this.inputValue != '') {
      this.updateSearch();
    }
  }

  checkUpdateSearch(e: any) {
    console.log('reached');
    if (e.key == 'Enter') {
      this.updateSearch();
    }
  }
}
