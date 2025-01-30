import { TitleCasePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { AccessStatus } from 'app/entities/enumerations/access-status.model';

@Pipe({
  name: 'displayAccessStatus',
})
export class DisplayAccessStatusPipe implements PipeTransform {
  constructor(private titleCasePipe: TitleCasePipe) {}

  transform(value: AccessStatus | null | undefined): unknown {
    if (value == null || value == undefined) {
      return `Inaccessible`;
    } else {
      return `${this.titleCasePipe.transform(value.split('_').join(' '))}`;
    }
  }
}
