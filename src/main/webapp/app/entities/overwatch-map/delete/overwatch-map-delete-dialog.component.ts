import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOverwatchMap } from '../overwatch-map.model';
import { OverwatchMapService } from '../service/overwatch-map.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './overwatch-map-delete-dialog.component.html',
})
export class OverwatchMapDeleteDialogComponent {
  overwatchMap?: IOverwatchMap;

  constructor(protected overwatchMapService: OverwatchMapService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.overwatchMapService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
