import { Inject, LOCALE_ID, Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

const SHORT_SPACE_DATE_FORMAT = 'd LLL YYYY';
const SHORT_SPACE_DATE_FORMAT_NO_YEAR = 'd LLL';
const FULL_SPACE_DATE_FORMAT = 'd LLLL YYYY';
const TIME_FORMAT = 'h:mma';
const MIDNIGHT = 'midnight';
const MIDDAY = 'midday';

/***
 * Pipe for formatting date and time when space is an issue and according to GDS specifications
 * The main format is the 'd LLL yyyy, h:mma'
 * If the time is 00:00 then the format is 'd LLL yyyy, \'midnight\''
 * If the time is 12:00 the the format is d LLL yyyy, \'midday\''
 *
 * Examples:
 * 12 November 2020 at 20:00 is formatted to 12 Nov 2020, 8:00pm
 * 12 November 2020 at 08:00 is formatted to 12 Nov 2020, 8:00am
 * 12 November 2020 at 00:00 is formatted to 12 Nov 2020, midnight
 * 12 November 2020 at 12:00 is formatted to 12 Nov 2020, midday
 */
@Pipe({
  name: 'gdsDateTimeShort',
})
export class GdsDateTimeShortPipe implements PipeTransform {
  constructor(@Inject(LOCALE_ID) private readonly locale: string) {}

  transform(value: any): string | null {
    if (!value) {
      return null;
    }
    const input: Date = new Date(value);
    let time = new DatePipe(this.locale)
      .transform(value, TIME_FORMAT)
      .toLowerCase();
    if (input.getHours() === 0 && input.getMinutes() === 0) {
      time = MIDNIGHT;
    } else if (input.getHours() === 12 && input.getMinutes() === 0) {
      time = MIDDAY;
    }
    return `${new DatePipe(this.locale).transform(
      value,
      this.getDateFormat()
    )}, ${time}`;
  }

  protected getDateFormat(): string {
    return SHORT_SPACE_DATE_FORMAT;
  }
}

/***
 * Pipe for formatting date and time according to GDS specifications
 * The main format is the 'd LLLL yyyy, h:mma'
 * If the time is 00:00 then the format is 'd LLLL yyyy, \'midnight\''
 * If the time is 12:00 the the format is d LLLL yyyy, \'midday\''
 *
 * Examples:
 * 12 November 2020 at 20:00 is formatted to 12 November 2020, 8:00pm
 * 12 November 2020 at 08:00 is formatted to 12 November 2020, 8:00am
 * 12 November 2020 at 00:00 is formatted to 12 November 2020, midnight
 * 12 November 2020 at 12:00 is formatted to 12 November 2020, midday
 */
@Pipe({
  name: 'gdsDateTime',
})
export class GdsDateTimePipe
  extends GdsDateTimeShortPipe
  implements PipeTransform
{
  protected getDateFormat(): string {
    return FULL_SPACE_DATE_FORMAT;
  }
}

/***
 * Pipe for formatting date when space is an issue and according to GDS specifications
 * The format is the 'd LLL yyyy'
 *
 * Example:
 * 12 November 2020 at 20:00 is formatted to 12 Nov 2020
 */
@Pipe({
  name: 'gdsDateShort',
})
export class GdsDateShortPipe implements PipeTransform {
  constructor(@Inject(LOCALE_ID) private readonly locale: string) {}

  transform(value: any) {
    return new DatePipe(this.locale).transform(value, SHORT_SPACE_DATE_FORMAT);
  }
}

/***
 * Pipe for formatting date according to GDS specifications
 * The format is the 'd LLLL yyyy'
 *
 * Example:
 * 12 November 2020 at 20:00 is formatted to 12 November 2020
 */
@Pipe({
  name: 'gdsDate',
})
export class GdsDatePipe implements PipeTransform {
  constructor(@Inject(LOCALE_ID) private readonly locale: string) {}

  transform(value: any) {
    return new DatePipe(this.locale).transform(value, FULL_SPACE_DATE_FORMAT);
  }
}

/***
 * Pipe for keeping day and month.
 *
 * Example:
 * 12 November 2020 at 20:00 is formatted to 12 Nov.
 */
@Pipe({
  name: 'gdsDateShortNoYear',
})
export class GdsDateShortNoYearPipe implements PipeTransform {
  constructor(@Inject(LOCALE_ID) private readonly locale: string) {}

  transform(value: any) {
    return new DatePipe(this.locale).transform(
      value,
      SHORT_SPACE_DATE_FORMAT_NO_YEAR
    );
  }
}

/***
 * Pipe for keeping only the time in GDS format
 */
@Pipe({
  name: 'gdsTime',
})
export class GdsTimePipe implements PipeTransform {
  constructor(@Inject(LOCALE_ID) private readonly locale: string) {}

  transform(value: any): string | null {
    if (!value) {
      return null;
    }
    const input: Date = new Date(value);
    let time = new DatePipe(this.locale)
      .transform(value, TIME_FORMAT)
      .toLowerCase();
    if (input.getHours() === 0 && input.getMinutes() === 0) {
      time = MIDNIGHT;
    } else if (input.getHours() === 12 && input.getMinutes() === 0) {
      time = MIDDAY;
    }
    return time;
  }
}

/***
 * Pipe for keeping only the UTC time in GDS format
 */
@Pipe({
  name: 'gdsTimeUTC',
})
export class GdsTimeUTCPipe implements PipeTransform {
  constructor(@Inject(LOCALE_ID) private readonly locale: string) {}

  transform(value: any): string | null {
    if (!value) {
      return null;
    }
    const input: Date = new Date(value);
    let time = new DatePipe(this.locale)
      .transform(value, TIME_FORMAT, 'UTC')
      .toLowerCase();
    if (input.getHours() === 0 && input.getMinutes() === 0) {
      time = MIDNIGHT;
    } else if (input.getHours() === 12 && input.getMinutes() === 0) {
      time = MIDDAY;
    }
    return `${time} UTC`;
  }
}

/***
 * Pipe for formatting date and time according to GDS specifications
 * The main format is the 'd LLLL yyyy, h:mma'
 * If the time is 00:00 then the format is 'd LLLL yyyy, \'midnight\''
 * If the time is 12:00 the the format is d LLLL yyyy, \'midday\''
 *
 * Examples:
 * 12 November 2020 at 20:00 is formatted to 12 November 2020, 8:00pm UTC
 * 12 November 2020 at 08:00 is formatted to 12 November 2020, 8:00am UTC
 * 12 November 2020 at 00:00 is formatted to 12 November 2020, midnight UTC
 * 12 November 2020 at 12:00 is formatted to 12 November 2020, midday UTC
 */
@Pipe({
  name: 'gdsDateTimeUTC',
})
export class GdsDateTimeUTCPipe implements PipeTransform {
  constructor(@Inject(LOCALE_ID) private readonly locale: string) {}

  transform(value: any): string | null {
    if (!value) {
      return null;
    }
    const input: Date = new Date(value);
    let time = new DatePipe(this.locale)
      .transform(value, TIME_FORMAT, 'UTC')
      .toLowerCase();
    if (input.getHours() === 0 && input.getMinutes() === 0) {
      time = MIDNIGHT;
    } else if (input.getHours() === 12 && input.getMinutes() === 0) {
      time = MIDDAY;
    }
    return `${new DatePipe(this.locale).transform(
      value,
      this.getDateFormat(),
      'UTC'
    )}, ${time} UTC`;
  }

  protected getDateFormat(): string {
    return FULL_SPACE_DATE_FORMAT;
  }
}
