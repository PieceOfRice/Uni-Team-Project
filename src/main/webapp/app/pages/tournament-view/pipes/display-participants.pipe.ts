import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'displayParticipants',
})
export class DisplayParticipantsPipe implements PipeTransform {
  transform(count: string | number | null | undefined, max: number | null | undefined): string {
    if (!count) {
      count = 0;
    }

    if (typeof count != 'number') {
      count = parseInt(count);
    }

    let participantsFormatted = '';
    if (count >= 0) {
      let pluralSuffix = '';
      if (count != 1) {
        pluralSuffix = 's';
      }
      participantsFormatted = `${count} Participant${pluralSuffix}`;
    } else {
      // having less than 0 participants should never happen
      return `${count} participants!??!?!`;
    }

    if (max && max > 0) {
      return `${participantsFormatted} out of ${max}`;
    }
    return participantsFormatted;
  }
}
