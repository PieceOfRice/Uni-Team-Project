import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITournamentAccessibility } from '../tournament-accessibility.model';
import { TournamentAccessibilityService } from '../service/tournament-accessibility.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tournament-accessibility-delete-dialog.component.html',
})
export class TournamentAccessibilityDeleteDialogComponent {
  tournamentAccessibility?: ITournamentAccessibility;

  constructor(protected tournamentAccessibilityService: TournamentAccessibilityService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tournamentAccessibilityService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
