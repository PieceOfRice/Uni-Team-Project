import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITournamentAccessibility } from '../tournament-accessibility.model';

@Component({
  selector: 'jhi-tournament-accessibility-detail',
  templateUrl: './tournament-accessibility-detail.component.html',
})
export class TournamentAccessibilityDetailComponent implements OnInit {
  tournamentAccessibility: ITournamentAccessibility | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tournamentAccessibility }) => {
      this.tournamentAccessibility = tournamentAccessibility;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
