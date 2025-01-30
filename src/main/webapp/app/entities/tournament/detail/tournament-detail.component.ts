import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITournament } from '../tournament.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-tournament-detail',
  templateUrl: './tournament-detail.component.html',
})
export class TournamentDetailComponent implements OnInit {
  tournament: ITournament | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tournament }) => {
      this.tournament = tournament;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
