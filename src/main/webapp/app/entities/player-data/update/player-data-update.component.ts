import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PlayerDataFormService, PlayerDataFormGroup } from './player-data-form.service';
import { IPlayerData } from '../player-data.model';
import { PlayerDataService } from '../service/player-data.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { PlayerLanguage } from 'app/entities/enumerations/player-language.model';
import { PlayerDevice } from 'app/entities/enumerations/player-device.model';

@Component({
  selector: 'jhi-player-data-update',
  templateUrl: './player-data-update.component.html',
})
export class PlayerDataUpdateComponent implements OnInit {
  isSaving = false;
  playerData: IPlayerData | null = null;
  playerLanguageValues = Object.keys(PlayerLanguage);
  playerDeviceValues = Object.keys(PlayerDevice);

  usersSharedCollection: IUser[] = [];

  editForm: PlayerDataFormGroup = this.playerDataFormService.createPlayerDataFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected playerDataService: PlayerDataService,
    protected playerDataFormService: PlayerDataFormService,
    protected userService: UserService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ playerData }) => {
      this.playerData = playerData;
      if (playerData) {
        this.updateForm(playerData);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('teamprojectApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const playerData = this.playerDataFormService.getPlayerData(this.editForm);
    if (playerData.id !== null) {
      this.subscribeToSaveResponse(this.playerDataService.update(playerData));
    } else {
      this.subscribeToSaveResponse(this.playerDataService.create(playerData));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlayerData>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(playerData: IPlayerData): void {
    this.playerData = playerData;
    this.playerDataFormService.resetForm(this.editForm, playerData);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, playerData.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.playerData?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
