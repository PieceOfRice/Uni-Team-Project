import { Component, OnInit } from '@angular/core';
import { MAT_SELECT_SCROLL_STRATEGY_PROVIDER } from '@angular/material/select';
import { AccessStatus } from 'app/entities/enumerations/access-status.model';
import { TournamentBracketType } from 'app/entities/enumerations/tournament-bracket-type.model';
import { VenueAccessibilities } from 'app/entities/enumerations/venue-accessibilities.model';
import { TournamentSetting } from 'app/entities/enumerations/tournament-setting.model';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TournamentService, EntityArrayResponseType } from 'app/entities/tournament/service/tournament.service';
import { TournamentAccessibilityService } from 'app/entities/tournament-accessibility/service/tournament-accessibility.service';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatCardModule } from '@angular/material/card';
import { AbstractControl, Validator, NG_VALIDATORS } from '@angular/forms';
import dayjs from 'dayjs/esm';
import { Router, ActivatedRoute } from '@angular/router';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { TournamentFormService, TournamentFormGroup } from 'app/entities/tournament/update/tournament-form.service';
import { PlayerDataService, EntityResponseType } from 'app/entities/player-data/service/player-data.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { firstValueFrom, lastValueFrom } from 'rxjs';
import { ThisReceiver } from '@angular/compiler';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

interface PrivacyValue {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'jhi-tournament-edit',
  templateUrl: './tournament-edit.component.html',
  styleUrls: ['./tournament-edit.component.scss', './../../../content/scss/_bootstrap-variables.scss'],
  providers: [MAT_SELECT_SCROLL_STRATEGY_PROVIDER],
})
export class TournamentEditComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private tournamentService: TournamentService,
    private router: Router,
    private dataUtils: DataUtils,
    private playerDataService: PlayerDataService,
    private accountService: AccountService,
    protected tournamentFormService: TournamentFormService
  ) {}

  selectedAccess: String = 'NOTHING';
  selectedLocation: String = 'NOTHING';
  extraDrawMatch: boolean = false;
  showBracketExamples: boolean = false;
  tournamentData: ITournament | null = null;
  tournamentId = -1;
  isStarted: boolean = false;
  isEnded: boolean = false;
  userId: number = -1;
  creatorUserId: number = -1;
  hasPermission: boolean = false;

  tournamentEditFormControl: TournamentFormGroup = this.tournamentFormService.createTournamentFormGroup();

  tournamentLocationOptions: PrivacyValue[] = [
    { value: TournamentSetting.IN_PERSON, viewValue: 'In Person' },
    { value: TournamentSetting.ONLINE, viewValue: 'Online' },
    { value: TournamentSetting.MIXED, viewValue: 'Mixed' },
  ];

  tournamentPrivacyOptions: PrivacyValue[] = [
    { value: AccessStatus.CLOSED, viewValue: AccessStatus.CLOSED },
    { value: AccessStatus.PRIVATE, viewValue: AccessStatus.PRIVATE },
    { value: AccessStatus.PUBLIC, viewValue: AccessStatus.PUBLIC },
  ];

  tournamentBracketFormats: PrivacyValue[] = [
    { value: TournamentBracketType.SINGLE_ELIMINATION, viewValue: 'Single Elimination' },
    { value: TournamentBracketType.DOUBLE_ELIMINATION, viewValue: 'Double Elimination' },
  ];

  get gamesPerMatch() {
    return this.tournamentEditFormControl.get('gamesPerMatch') as FormControl;
  }

  get maxParticipants() {
    return this.tournamentEditFormControl.get('maxParticipants') as FormControl;
  }

  get name() {
    return this.tournamentEditFormControl.get('name') as FormControl;
  }

  get ended() {
    return this.tournamentEditFormControl.get('ended') as FormControl;
  }

  get started() {
    return this.tournamentEditFormControl.get('isLive') as FormControl;
  }

  changeExtra(): void {
    this.extraDrawMatch = !this.extraDrawMatch;
  }

  changeEnded(): void {
    this.isEnded = !this.isEnded;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.tournamentEditFormControl, field, isImage).subscribe(
      success => {
        console.log(success);
      },
      error => {
        console.error(error);
      }
    );
  }

  saveFormResults(): void {
    if (this.canSubmitResults() && this.hasPermission) {
      if (this.extraDrawMatch) {
        this.gamesPerMatch.setValue(this.gamesPerMatch.value + 1);
      }

      this.ended.setValue(this.isEnded);

      if (this.isEnded && this.isStarted) {
        this.tournamentEditFormControl.get('isLive')?.setValue(false);
      } else if (this.isStarted) {
        console.log('setting true');
        this.tournamentEditFormControl.get('isLive')?.setValue(true);
      }

      const updatedTournament = this.tournamentFormService.getTournament(this.tournamentEditFormControl);
      console.log(updatedTournament);
      if (updatedTournament.id !== null) {
        this.tournamentService.update(updatedTournament).subscribe({
          next: newTournament => this.finishTournamentEdit(newTournament),
          error: () => this.cancelTournamentEdit(),
        });
      }
    }
  }

  canSubmitResults(): boolean {
    return this.tournamentEditFormControl.valid;
  }

  finishTournamentEdit(newTournament: EntityResponseType): void {
    window.history.back();
  }

  cancelTournamentEdit(): void {
    window.history.back();
  }

  validateGamesPerMatch(): void {
    if (this.gamesPerMatch.value !== null) {
      console.log('games: ', this.gamesPerMatch.value);
      if (this.gamesPerMatch.value % 2 != 1) {
        this.tournamentEditFormControl.get('gamesPerMatch')?.setErrors({ notOdd: true });
        return;
      } else if (this.gamesPerMatch.value > 128) {
        this.tournamentEditFormControl.get('gamesPerMatch')?.setErrors({ tooBig: true });
        return;
      }
    }
    this.tournamentEditFormControl.get('gamesPerMatch')?.setErrors(null);
  }

  validateParticipants(): void {
    if (this.maxParticipants.value !== null) {
      if (this.maxParticipants.value > 128) {
        this.tournamentEditFormControl.get('maxParticipants')?.setErrors({ tooBig: true });
        return;
      }
    }
    this.tournamentEditFormControl.get('maxParticipants')?.setErrors(null);
  }

  setIsStarted(override: boolean | undefined | null): void {
    if (override == true) {
      this.isStarted = true; // auto-changes the ui to the end-edit version
      this.tournamentEditFormControl.get('isLive')?.setValue(true);
      this.tournamentEditFormControl.get('startTime')?.setValue(dayjs().format(DATE_TIME_FORMAT));
      return;
    }
    if (this.tournamentData?.startTime) {
      if (this.tournamentData.isLive != null) {
        this.isStarted = this.tournamentData?.isLive;
      }
    }
  }

  async setTournamentId(): Promise<void> {
    const params = await firstValueFrom(this.route.params);
    this.tournamentId = parseInt(params['id']);
    console.log(this.tournamentId);
  }

  async setTournament(): Promise<void> {
    try {
      const data = await lastValueFrom(this.tournamentService.find(this.tournamentId));
      console.log(data.body);
      this.tournamentData = data.body;
    } catch {
      console.log('Tournament not found');
      this.router.navigate(['404']);
    }
  }

  async setAccount(): Promise<void> {
    if (!this.accountService.hasAnyAuthority('ROLE_USER')) {
      this.router.navigate(['404']);
    }
    const account = await firstValueFrom(this.accountService.getAuthenticationState());
    console.log('account :' + account);
    if (account == null) {
      console.log('Account is null.');
      this.router.navigate(['404']);
    } else {
      this.userId = (account as any).id;
    }
    if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
      this.hasPermission = true;
    }
  }

  async setCreatorUserId(): Promise<void> {
    const data: EntityResponseType = await firstValueFrom(this.playerDataService.find(this.tournamentData!.creator!.id));
    if (data.body?.user?.id) {
      this.creatorUserId = data.body?.user?.id;
    }
  }

  async ngOnInit(): Promise<void> {
    this.extraDrawMatch = false;
    this.isEnded = false;

    await this.setTournamentId();
    console.log('Found tournament id:', this.tournamentId);
    await this.setTournament();
    console.log('Found tournament data:', this.tournamentData);
    await this.setAccount();
    console.log('Found account:', this.userId);

    if (!this.hasPermission) {
      await this.setCreatorUserId();
      console.log('Found: ' + this.creatorUserId);
      if (this.userId == this.creatorUserId) {
        this.hasPermission = true;
      }
    }

    if (!this.hasPermission || !this.tournamentData) {
      this.router.navigate(['404']);
    }

    if (this.tournamentData) {
      this.tournamentFormService.resetForm(this.tournamentEditFormControl, this.tournamentData);
      this.setIsStarted(undefined);
      this.selectedAccess = this.tournamentData.accessStatus!;
      this.selectedLocation = this.tournamentData.tournamentSetting!;

      if (!this.isStarted) {
        if (this.tournamentData.gamesPerMatch! % 2 == 0) {
          this.gamesPerMatch.setValue(this.tournamentData.gamesPerMatch! - 1);
          this.extraDrawMatch = true;
        }
      }
    }
  }
}
