import { TitleCasePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'displayMoney',
})
export class DisplayMoneyPipe implements PipeTransform {
  ValidMoneySuffixes = {
    Prize: 'prize',
    Entry: 'entry',
  };
  CurrencySymbol = '£';

  constructor(private titleCasePipe: TitleCasePipe) {}

  transform(value: number | null | undefined, suffix: string | undefined): string {
    if (value == undefined || value == null) {
      value = 0;
    }

    if (value > 0) {
      let extraSuffix = '';
      // checks if the given suffix exists and is a valid suffix argument
      if (suffix != undefined && Object.values(this.ValidMoneySuffixes).indexOf(suffix) > -1) {
        extraSuffix = ` ${this.titleCasePipe.transform(suffix)}`;
      }

      return `${this.CurrencySymbol}${value}${extraSuffix}`;
    } else if (suffix == this.ValidMoneySuffixes.Entry) {
      return 'Free!';
    } else if (suffix == this.ValidMoneySuffixes.Prize) {
      return 'No Prize';
    } else {
      return 'Unknown';
    }
  }
}
