import { Pipe, PipeTransform } from '@angular/core';
import dayjs from 'dayjs/esm';

const STANDARD_TIME_FORMAT = 'ddd, DD-MMM-YYYY HH:mm';
const TIME_ZONE_FORMAT = 'Z[Z]';

@Pipe({
  name: 'displayTime',
})
export class DisplayTimePipe implements PipeTransform {
  // just formats a given time by the required format
  formatTime(time: dayjs.Dayjs): string {
    let chosenFormat = STANDARD_TIME_FORMAT;
    if (time.utcOffset() != 0) {
      chosenFormat = `${STANDARD_TIME_FORMAT} ${TIME_ZONE_FORMAT}`;
    }
    return time.format(chosenFormat);
  }

  // formats 2 times into a single string, intelligently figuring out the difference between them
  // roughly into this if on the same day     : dd-MMM-YYYY HH:mm - HH:mm
  // or into this if they are different days  : dd-MMM-YYYY HH:mm - dd-MMM HH:mm
  formatDifference(time1: dayjs.Dayjs, time2: dayjs.Dayjs): string {
    let isOnDifferentDay = time2.diff(time1, 'day') != 0;
    let endTimeFormat = time2.format('HH:mm');
    if (isOnDifferentDay) {
      endTimeFormat = time2.format('DD-MMM HH:mm');
    }
    return `${this.formatTime(time1)} --- ${endTimeFormat}`;
  }

  transform(startTime: dayjs.Dayjs | null | undefined, endTime: dayjs.Dayjs | null | undefined): string {
    if (startTime && endTime) {
      return `${this.formatDifference(startTime, endTime)}`;
    } else if (startTime) {
      return `Starts ${this.formatTime(startTime)}`;
    } else if (endTime) {
      return `Ends ${this.formatTime(endTime)}`;
    } else {
      return 'Unknown Time';
    }
  }
}
