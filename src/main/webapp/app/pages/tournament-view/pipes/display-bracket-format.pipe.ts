import { TitleCasePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { TournamentBracketType } from 'app/entities/enumerations/tournament-bracket-type.model';

@Pipe({
  name: 'displayBracketFormat',
})
export class DisplayBracketFormatPipe implements PipeTransform {
  constructor(private titleCasePipe: TitleCasePipe) {}

  transform(value: TournamentBracketType | null | undefined): string {
    if (value == null || value == undefined) {
      return `Unknown Bracket Format`;
    } else {
      return `${this.titleCasePipe.transform(value.split('_').join(' '))}`;
    }
  }
}
