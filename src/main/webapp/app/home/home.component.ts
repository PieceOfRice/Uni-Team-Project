import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { HostListener } from '@angular/core';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { TITLE } from 'app/app.constants';
import { PageEvent } from '@angular/material/paginator';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { TournamentService } from 'app/entities/tournament/service/tournament.service';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  title = TITLE;
  numberTopTeams = 4;
  pageSizeTopTeams = 5;
  pageSizePopularTournaments = 3;
  pageIndexTopTeams = 0;

  pageIndexPopularTournaments = 0;

  topTeams: ITeam[] = [];
  sliceTopTeams: ITeam[] = [];

  numberPopularTournaments = 0;
  popularTournaments: ITournament[] = [];
  slicePopularTournaments: ITournament[] = [];

  private readonly destroy$ = new Subject<void>();

  constructor(
    private accountService: AccountService,
    private router: Router,
    private teamService: TeamService,
    private tournamentService: TournamentService
  ) {}

  ngOnInit(): void {
    this.teamService.findTopteams().subscribe({
      next: res => {
        if (res.body) {
          this.topTeams = res.body;
          this.numberTopTeams = this.topTeams.length;
          this.updateSliceTopTeams(0);
        }
      },
      error: err => {
        console.error('Error fetching top teams:', err);
      },
    });

    this.tournamentService.findPopularTournaments().subscribe({
      next: res => {
        if (res.body) {
          this.popularTournaments = res.body;
          this.numberPopularTournaments = this.popularTournaments.length;
          this.updateSlicePopularTournaments(0);
        }
      },
      error: err => {
        console.error('Error fetching top teams:', err);
      },
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: string): void {
    this.pageSizeTopTeams = 2;
    this.pageSizePopularTournaments = 1;
    if (window.innerWidth > 600) {
      this.pageSizeTopTeams = Math.floor((window.innerWidth / 600) * 2);
      this.pageSizePopularTournaments = Math.floor(window.innerWidth / 600);
    }
    this.updateSliceTopTeams(this.pageIndexTopTeams);
    this.updateSlicePopularTournaments(this.pageIndexPopularTournaments);
  }

  getRandomMapImage(offset: number): string {
    return 'url(../../content/images/map' + (((Math.floor(Date.now() / 1000 / 60) + offset) % 14) + 1).toString() + '.png)';
  }

  updateSliceTopTeams(pageIndex: number): void {
    const startIndex = pageIndex * this.pageSizeTopTeams;
    let endIndex = startIndex + this.pageSizeTopTeams;
    if (endIndex > this.numberTopTeams) {
      endIndex = this.numberTopTeams;
    }
    this.sliceTopTeams = this.topTeams.slice(startIndex, endIndex);
  }
  updateSlicePopularTournaments(pageIndex: number): void {
    const startIndex = pageIndex * this.pageSizePopularTournaments;
    let endIndex = startIndex + this.pageSizePopularTournaments;
    if (endIndex > this.numberPopularTournaments) {
      endIndex = this.numberPopularTournaments;
    }
    this.slicePopularTournaments = this.popularTournaments.slice(startIndex, endIndex);
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  handleTopTeamsPageEvent(event: PageEvent): void {
    this.pageIndexTopTeams = event.pageIndex;
    this.updateSliceTopTeams(event.pageIndex);
    console.log(event);
    console.log(this.topTeams);
    console.log(this.sliceTopTeams);
  }

  handlePopularTournamentsPageEvent(event: PageEvent): void {
    this.pageIndexPopularTournaments = event.pageIndex;
    this.updateSlicePopularTournaments(event.pageIndex);
    console.log(event);
    console.log(this.popularTournaments);
    console.log(this.slicePopularTournaments);
  }
}
