import { Component, Input, OnInit } from '@angular/core';
import { BracketPosition } from '../tournament-view.component';
import { IMatch } from 'app/entities/match/match.model';
import { Router } from '@angular/router';

const ADD_DEFAULT_DATA = false; // should be turned off in production

export interface ParticipantBracketData {
  name: string;
  rank: number;
}

@Component({
  selector: 'jhi-bracket',
  templateUrl: './bracket.component.html',
  styleUrls: ['./bracket.component.scss'],
})
export class BracketComponent implements OnInit {
  @Input() position: BracketPosition = {
    bracket: 1,
    X: 0.5,
    Y: 0.5,
  };
  @Input() match: IMatch | undefined;

  @Input() topParticipant: ParticipantBracketData | undefined;
  @Input() bottomParticipant: ParticipantBracketData | undefined;
  topScore: string | undefined;
  bottomScore: string | undefined;
  winner: 'top' | 'bottom' | 'neither' = 'neither';

  styling = {};

  constructor(private router: Router) {}

  canNavigateToMatchPage(): boolean {
    return this.match != undefined && this.topParticipant != undefined && this.bottomParticipant != undefined;
  }

  ngOnInit(): void {
    this.styling = {
      left: `${this.position.X * 100}%`,
      top: `${this.position.Y * 100}%`,
    };

    // assigning match information
    if (this.match && this.match.scoreSubmitted == true) {
      const scoreSuffix = this.match.scoreApproved ? '' : '?';
      if (this.match.oneScore != null) {
        this.topScore = `${this.match.oneScore}${scoreSuffix}`;
      }
      if (this.match.twoScore != null) {
        this.bottomScore = `${this.match.twoScore}${scoreSuffix}`;
      }

      if (this.match.scoreApproved == true && this.match.oneScore != null && this.match.twoScore != null) {
        this.winner = this.match.oneScore > this.match.twoScore ? 'top' : 'bottom';
      }
    }

    // useful for testing the look and feel of the bracket by injecting default data in places where it is not provided
    if (ADD_DEFAULT_DATA) {
      if (this.topParticipant == undefined) {
        this.topParticipant = {
          name: 'Top Participant Name',
          rank: 1,
        };
      }

      if (this.topScore == undefined) {
        this.topScore = '0';
      }

      if (this.bottomParticipant == undefined) {
        this.bottomParticipant = {
          name: 'Bottom Participant Name',
          rank: 2,
        };
      }

      if (this.bottomScore == undefined) {
        this.bottomScore = '0';
      }
    }
  }

  navigateToMatchPage() {
    if (this.canNavigateToMatchPage()) {
      this.router.navigate(['/match-view', this.match?.id]);
    }
  }
}
