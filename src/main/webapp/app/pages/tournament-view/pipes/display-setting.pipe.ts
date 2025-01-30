import { TitleCasePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { TournamentSetting } from 'app/entities/enumerations/tournament-setting.model';

@Pipe({
  name: 'displaySetting',
})
export class DisplaySettingPipe implements PipeTransform {
  constructor(private titleCasePipe: TitleCasePipe) {}

  transform(setting: TournamentSetting | null | undefined): string {
    if (setting == null || setting == undefined) {
      return `Unknown`;
    } else {
      return `${this.titleCasePipe.transform(setting.split('_').join(' '))}`;
    }
  }
}
