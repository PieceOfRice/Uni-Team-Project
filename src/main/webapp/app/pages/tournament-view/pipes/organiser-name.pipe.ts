import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'organiserName',
})
export class OrganiserNamePipe implements PipeTransform {
  transform(name: string | null | undefined, overwatchName: string | null | undefined): string {
    if (name && overwatchName) {
      return `${name} (@${overwatchName})`;
    } else if (name) {
      return name;
    } else if (overwatchName) {
      return `@${overwatchName}`;
    } else {
      return '';
    }
  }
}
