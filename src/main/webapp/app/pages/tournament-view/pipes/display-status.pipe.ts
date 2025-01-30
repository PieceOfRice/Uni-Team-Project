import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'displayStatus',
})
export class DisplayStatusPipe implements PipeTransform {
  transform(isLive: boolean | null | undefined, ended: boolean | null | undefined): string {
    if (ended) {
      return 'Ended';
    } else if (isLive) {
      return 'Started';
    } else {
      return 'Beginning soon...';
    }
  }
}
