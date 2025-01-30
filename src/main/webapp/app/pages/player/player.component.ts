import { TeamService } from './../../entities/team/service/team.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';
import { TeamPlayerService } from 'app/entities/team-player/service/team-player.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { MatchService } from 'app/entities/match/service/match.service';
import { faUser } from '@fortawesome/free-solid-svg-icons';
import { IMatch } from 'app/entities/match/match.model';
import { ITeam } from 'app/entities/team/team.model';

interface Stat {
  label: string;
  value: number | null | undefined;
}

@Component({
  selector: 'jhi-player',
  templateUrl: './player.component.html',
  styleUrls: ['./player.component.scss'],
})
export class PlayerComponent implements OnInit {
  defaultOrganiserIcon = faUser;
  playerData: IPlayerData | null = null;
  playerId: number | null = null;
  organiser: IPlayerData | null = null;

  playerName: string | null = null;
  playerUsername: string | null = null;
  playerBio: string | null = null;
  playerLanguages: string | null = null;
  playerDevices: string | null = null;

  account: Account | null = null;
  accountPD: any;

  stats: Stat[] = [];

  // Player teams
  currentTeams: ITeam[] = [];

  // Tournament history
  tournamentHistory: string[] = ['Tournament1', 'Tournament2', 'Tournament3'];

  //match history
  matchHistory: IMatch[] = [];
  //matchHistory: IMatch[] = [];
  victoryMatches: IMatch[] = [];

  constructor(
    private route: ActivatedRoute,
    private playerDataService: PlayerDataService,
    private matchService: MatchService,
    private teamPlayerService: TeamPlayerService,
    private accountService: AccountService,
    private teamService: TeamService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.playerId = +id;
        this.loadPlayerData(this.playerId);
        this.loadMatchHistory(this.playerId);
        this.loadCurrentTeams(this.playerId);
      }
    });

    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
  }

  private loadPlayerData(playerId: number): void {
    this.playerDataService.find(playerId).subscribe({
      next: response => {
        this.playerData = response.body;
        if (this.playerData) {
          // Initialize stats if player data is available
          this.stats = [
            { label: 'MATCHES PLAYED', value: this.playerData.matchesPlayed },
            { label: 'MATCHES WON', value: this.playerData.matchWins },
            { label: 'TOURNAMENTS PLAYED', value: this.playerData.tournamentsPlayed },
            { label: 'TOURNAMENTS WON', value: this.playerData.tournamentWins },
            { label: 'TOURNAMENT TOP 10s', value: this.playerData.tournamentTop10s },
          ];
          this.playerName = this.playerData.name ? this.playerData.name.toString() : null;
          this.playerUsername = this.playerData.overwatchUsername ? this.playerData.overwatchUsername.toString() : null;
          this.playerBio = this.playerData.bio ? this.playerData.bio.toString() : null;
          this.playerLanguages = this.playerData.primaryLanguage?.toString() ?? null;
          this.playerDevices = this.playerData.device ? this.playerData.device.toString() : null;
        }
      },
      error: error => {
        console.error('Error fetching player data:', error);
        this.setDefaultValues();
      },
    });
  }

  private loadMatchHistory(playerId: number): void {
    this.matchService.findAllByPlayerId(playerId).subscribe({
      next: res => {
        if (res.body) {
          if (res.body.length > 0) {
            this.matchHistory = res.body;
            console.log('Successfully fetched matches');
            console.log(this.matchHistory);

            this.stats[0] = { label: 'MATCHES PLAYED', value: this.matchHistory.length };

            this.matchHistory.forEach(match => {
              if (match.oneScore == null || match.twoScore == null) {
                return;
              }
              let winnerTeamId = match.teamOne;
              if (match.twoScore > match.oneScore) {
                winnerTeamId = match.teamTwo;
              }

              this.victoryMatches.push(match);
            });
          }
        }
      },
      error: err => {
        console.error('Error fetching matches for player:', err);
        this.matchHistory = []; // On error, also set to an empty array
      },
    });
  }

  private loadCurrentTeams(playerId: number): void {
    this.teamService.findAllByPlayerId(playerId).subscribe({
      next: res => {
        if (res.body) {
          if (res.body.length > 0) {
            this.currentTeams = res.body;
            console.log('Successfully fetched teams');
            console.log(this.currentTeams);
          }
        }
      },
      error: err => {
        console.error('Error fetching teams for player:', err);
        this.currentTeams = [];
      },
    });
  }

  private setDefaultValues(): void {
    // Set default or placeholder values
    this.playerName = 'Not Available';
    this.playerUsername = 'Not Available';
    this.playerBio = 'No Bio Available';
    this.playerLanguages = 'None';
    this.playerDevices = 'None';
    this.stats = [
      { label: 'MATCHES PLAYED', value: 0 },
      { label: 'MATCHES WON', value: 0 },
      { label: 'TOURNAMENTS PLAYED', value: 0 },
      { label: 'TOURNAMENTS WON', value: 0 },
      { label: 'TOURNAMENT TOP 10s', value: 0 },
    ];
  }
}
