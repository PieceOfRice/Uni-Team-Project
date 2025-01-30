import { Component, OnInit } from '@angular/core';
import { MAT_SELECT_SCROLL_STRATEGY_PROVIDER } from '@angular/material/select';
import { AccessStatus } from 'app/entities/enumerations/access-status.model';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TeamService } from 'app/entities/team/service/team.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { Router } from '@angular/router';

interface PrivacyValue {
  value: string;
  viewValue: string;
}

const DEFAULTS = {
  id: null,
  name: null,
  description: null,
  accessStatus: null,
  logo: null,
  logoContentType: null,
  banner: null,
  bannerContentType: null,
};

@Component({
  selector: 'jhi-team-create',
  templateUrl: './team-create.component.html',
  styleUrls: ['./team-create.component.scss'],
  providers: [MAT_SELECT_SCROLL_STRATEGY_PROVIDER],
})
export class TeamCreateComponent implements OnInit {
  teamPrivacyOptions: PrivacyValue[] = [
    { value: AccessStatus.CLOSED, viewValue: AccessStatus.CLOSED },
    { value: AccessStatus.PRIVATE, viewValue: AccessStatus.PRIVATE },
    { value: AccessStatus.PUBLIC, viewValue: AccessStatus.PUBLIC },
  ];

  teamCreationFormControl = new FormGroup({
    id: new FormControl({ value: null, disabled: true }),
    name: new FormControl(DEFAULTS.name, { validators: [Validators.required] }),
    description: new FormControl(DEFAULTS.description),
    accessStatus: new FormControl(DEFAULTS.accessStatus, { validators: [Validators.required] }),
    logo: new FormControl(DEFAULTS.logo),
    logoContentType: new FormControl(DEFAULTS.logoContentType),
    banner: new FormControl(DEFAULTS.banner),
    bannerContentType: new FormControl(DEFAULTS.bannerContentType),
  });

  constructor(private teamService: TeamService, private dataUtils: DataUtils, private router: Router) {}

  saveFormResults(): void {
    if (this.canSubmitResults()) {
      this.teamService.create(this.teamCreationFormControl.getRawValue()).subscribe({
        next: newTeam => {
          this.router.navigate(['team-view/' + newTeam.body?.id]);
        },
        error: () => this.finishTeamCreation(),
      });
    }
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.teamCreationFormControl, field, isImage).subscribe(
      success => {
        console.log(success);
      },
      error => {
        console.error(error);
      }
    );
  }

  // checks whether the team creation form can be submitted
  canSubmitResults(): boolean {
    return this.teamCreationFormControl.valid;
  }

  finishTeamCreation(): void {
    // should route back to the previous page
    window.history.back();
  }

  cancelTeamCreation(): void {
    this.finishTeamCreation();
  }

  ngOnInit(): void {}
}
