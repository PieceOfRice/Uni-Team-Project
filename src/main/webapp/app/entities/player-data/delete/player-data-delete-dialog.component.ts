import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlayerData } from '../player-data.model';
import { PlayerDataService } from '../service/player-data.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './player-data-delete-dialog.component.html',
})
export class PlayerDataDeleteDialogComponent {
  playerData?: IPlayerData;

  constructor(protected playerDataService: PlayerDataService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.playerDataService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
