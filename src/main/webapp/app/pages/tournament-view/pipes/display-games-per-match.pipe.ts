import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'displayGamesPerMatch',
})
export class DisplayGamesPerMatchPipe implements PipeTransform {
  transform(gamesPerMatch: number | null | undefined): string {
    if (!gamesPerMatch) {
      return 'Unknown';
    } else {
      return `${gamesPerMatch} Games per Match`;
    }
  }
}
