import { Pipe, PipeTransform } from '@angular/core';
import { TitleCasePipe } from '@angular/common';

/**
 * Maps the role authorities from an `account.authorities` table into a nicer, more user-friendly form.
 * Essentially, it turns `ROLE_ADMIN` into `Admin`
 */
@Pipe({
  name: 'mapRoleAuthority',
  pure: true,
})
export class MapRoleAuthority implements PipeTransform {
  constructor(private titleCasePipe: TitleCasePipe) {}

  transform(value: string, ...args: any[]): string {
    if (!value) return '';
    return this.titleCasePipe.transform(value.split('_')[1]);
  }
}
