import { Component, OnInit } from '@angular/core';
import { MAT_SELECT_SCROLL_STRATEGY_PROVIDER } from '@angular/material/select';
import { AccessStatus } from 'app/entities/enumerations/access-status.model';
import { TournamentBracketType } from 'app/entities/enumerations/tournament-bracket-type.model';
import { VenueAccessibilities } from 'app/entities/enumerations/venue-accessibilities.model';
import { TournamentSetting } from 'app/entities/enumerations/tournament-setting.model';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { EntityResponseType, TournamentService, EntityArrayResponseType } from 'app/entities/tournament/service/tournament.service';
import { TournamentAccessibilityService } from 'app/entities/tournament-accessibility/service/tournament-accessibility.service';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatCardModule } from '@angular/material/card';
import { AbstractControl, Validator, NG_VALIDATORS } from '@angular/forms';
import dayjs from 'dayjs/esm';
import { Router } from '@angular/router';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { AccountService } from 'app/core/auth/account.service';
import { Dayjs } from 'dayjs';

interface PrivacyValue {
  value: string;
  viewValue: string;
}

const DEFAULTS = {
  id: null,
  name: null,
  description: null,
  prizePool: null,
  entryFee: null,
  startTime: null,
  endTime: null,
  location: null,
  bracketFormat: null,
  accessStatus: null,
  isLive: false,
  ended: false,
  banner: null,
  bannerContentType: null,
  gamesPerMatch: 3,
  maxParticipants: null,
  tournamentSetting: null,

  accessibility: [],
  tournament: null,
};

@Component({
  selector: 'jhi-tournament-create',
  templateUrl: './tournament-create.component.html',
  styleUrls: ['./tournament-create.component.scss', './../../../content/scss/_bootstrap-variables.scss'],
  providers: [MAT_SELECT_SCROLL_STRATEGY_PROVIDER],
})
export class TournamentCreateComponent implements OnInit {
  constructor(
    private tournamentService: TournamentService,
    private router: Router,
    private tournamentAccessibilityService: TournamentAccessibilityService,
    private dataUtils: DataUtils,
    private accountService: AccountService
  ) {}

  selectedAccess: String = 'NOTHING';
  selectedLocation: String = 'NOTHING';
  detailedCreationVisible: boolean = false;
  extraDrawMatch: boolean = false;
  nameNotUnique: boolean = true;
  showBracketExamples: boolean = false;
  startBeforeEnd: boolean = true;

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

  tournamentAccessibilityOptions: PrivacyValue[] = [
    { value: VenueAccessibilities.ACCESSIBLE_PARKING, viewValue: 'Accessible Parking' },
    { value: VenueAccessibilities.LIFTS, viewValue: VenueAccessibilities.LIFTS },
    { value: VenueAccessibilities.RAMPS, viewValue: VenueAccessibilities.RAMPS },
  ];

  tournamentCreationFormControl = new FormGroup({
    id: new FormControl({ value: null, disabled: true }),
    name: new FormControl(DEFAULTS.name, { validators: [Validators.required, Validators.minLength(3), Validators.maxLength(40)] }),
    description: new FormControl(DEFAULTS.description, { validators: [Validators.maxLength(5000)] }),
    prizePool: new FormControl(DEFAULTS.prizePool),
    entryFee: new FormControl(DEFAULTS.entryFee),
    startTime: new FormControl(DEFAULTS.startTime, { validators: [Validators.required, validateFuture, validateDate] }),
    endTime: new FormControl(DEFAULTS.endTime, { validators: [validateFuture] }),
    location: new FormControl(DEFAULTS.location, { validators: [Validators.maxLength(5000)] }),
    bracketFormat: new FormControl(DEFAULTS.bracketFormat, { validators: [Validators.required] }),
    accessStatus: new FormControl(DEFAULTS.accessStatus, { validators: [Validators.required] }),
    ended: new FormControl(DEFAULTS.ended),
    isLive: new FormControl(DEFAULTS.isLive),
    banner: new FormControl(DEFAULTS.banner),
    bannerContentType: new FormControl(DEFAULTS.bannerContentType),
    gamesPerMatch: new FormControl(DEFAULTS.gamesPerMatch, { validators: [Validators.required, validateNumber, validateOdd] }),
    maxParticipants: new FormControl(DEFAULTS.maxParticipants, { validators: [validateNumber] }),
    tournamentSetting: new FormControl(DEFAULTS.tournamentSetting, { validators: [Validators.required] }),

    /*Not sure if this goes here*/
    accessibility: new FormControl(DEFAULTS.accessibility),
    tournament: new FormControl(DEFAULTS.tournament),
  });

  get gamesPerMatch() {
    return this.tournamentCreationFormControl.get('gamesPerMatch') as FormControl;
  }

  get accessibility() {
    return this.tournamentCreationFormControl.get('accessibility') as FormControl;
  }

  get name() {
    return this.tournamentCreationFormControl.get('name') as FormControl;
  }

  get startTime() {
    return this.tournamentCreationFormControl.get('startTime') as FormControl;
  }

  get endTime() {
    return this.tournamentCreationFormControl.get('endTime') as FormControl;
  }

  changeExtra(): void {
    this.extraDrawMatch = !this.extraDrawMatch;
  }

  changeDetailedCreationVisible(): void {
    this.extraDrawMatch = false;
    this.detailedCreationVisible = !this.detailedCreationVisible;
  }

  checkNameUnique(): void {
    this.tournamentService.findOneByName(this.name.value).subscribe({
      next: foundTournamnet => this.checkFoundTournament(foundTournamnet),
      error: () => this.cancelTournamentCreation(),
    });
  }

  checkFoundTournament(foundTournamnet: EntityArrayResponseType): void {
    console.log('count: ', foundTournamnet.body?.length);
    if (foundTournamnet.body?.length! >= 1) {
      this.nameNotUnique = true;
      this.tournamentCreationFormControl.get('name')?.setErrors({ nameNotUnique: this.nameNotUnique });
    } else {
      this.tournamentCreationFormControl.get('name')?.setErrors(null);
      this.nameNotUnique = false;
      if (this.name.value.length < 3) {
        this.tournamentCreationFormControl.get('name')?.setErrors({ minlength: true });
      }
    }
    console.log(this.nameNotUnique);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.tournamentCreationFormControl, field, isImage).subscribe(
      success => {
        console.log(success);
      },
      error => {
        console.error(error);
      }
    );
  }

  saveFormResults(): void {
    if (this.canSubmitResults()) {
      if (this.extraDrawMatch) {
        this.tournamentCreationFormControl.get('gamesPerMatch')?.setValue(this.gamesPerMatch.value + 1);
      }

      console.log(this.tournamentCreationFormControl.value);
      console.log(this.extraDrawMatch);
      console.log(this.accessibility.getRawValue());
      this.tournamentService.create(this.tournamentCreationFormControl.getRawValue()).subscribe({
        next: newTournament => this.finishTournamentCreation(newTournament),
        error: () => this.cancelTournamentCreation(),
      });
    }
  }

  // checks whether the tournament creation form can be submitted
  canSubmitResults(): boolean {
    return this.tournamentCreationFormControl.valid && !this.nameNotUnique;
  }

  finishTournamentCreation(newTournament: EntityResponseType): void {
    console.log(newTournament.body?.id);

    console.log(':)) ' + this.accessibility.value);
    this.accessibility.value.forEach((accessibilityOption: VenueAccessibilities) => {
      console.log(':)) ' + accessibilityOption);
      this.tournamentAccessibilityService
        .create({ id: null, accessibility: accessibilityOption, tournament: newTournament.body })
        .subscribe(
          success => {
            console.log(success);
          },
          error => {
            console.error(error);
          }
        );
    });

    this.router.navigate(['tournament-view/' + newTournament.body?.id]);
  }

  cancelTournamentCreation(): void {
    window.history.back();
  }

  ngOnInit() {
    if (!this.accountService.hasAnyAuthority('ROLE_USER')) {
      this.router.navigate(['404']);
    }

    this.selectedAccess = AccessStatus.CLOSED;
    this.selectedLocation = TournamentSetting.IN_PERSON;
    this.detailedCreationVisible = false;
    this.extraDrawMatch = false;
  }
}

function validateOdd(control: AbstractControl): { [key: string]: any } | null {
  if (control.value % 2 != 1) {
    return { notOdd: true };
  }
  return null;
}

function validateNumber(control: AbstractControl): { [key: string]: any } | null {
  if (control.value > 128) {
    return { tooBig: true };
  }
  return null;
}

function validateDate(control: AbstractControl): { [key: string]: any } | null {
  if (!dayjs(control.value).isValid()) {
    return { invalidDate: true };
  }
  return null;
}

function validateFuture(control: AbstractControl): { [key: string]: any } | null {
  if (!dayjs().isBefore(control.value) && dayjs(control.value).isValid()) {
    return { notFuture: true };
  }
  return null;
}
