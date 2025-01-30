import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'displayLocation',
})
export class DisplayLocationPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    return value != null && value != undefined && value != '' ? value : 'Unknown';
  }
}
