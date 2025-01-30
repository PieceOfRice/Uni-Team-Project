import { Component, Input, OnInit } from '@angular/core';
import { SignedUpTeam } from '../tournament-view.component';
import { faUsers } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'jhi-participant',
  templateUrl: './participant.component.html',
  styleUrls: ['./participant.component.scss'],
})
export class ParticipantComponent implements OnInit {
  defaultTeamLogo = faUsers;

  @Input() data: SignedUpTeam | undefined;

  ngOnInit(): void {}
}
