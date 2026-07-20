import { Inject, LOCALE_ID, Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';
import { dayjsFormatDate } from '../shared.util';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';
import 'dayjs/locale/en-gb';
import { TIMEZONE, UK_ETS_DEFAULT_LOCALE } from '@registry-web/app.tokens';

dayjs.extend(utc);
dayjs.extend(timezone);

const SHORT_SPACE_DATE_FORMAT = 'D MMM YYYY';
const FULL_SPACE_DATE_FORMAT = 'D MMMM YYYY';
const SHORT_SPACE_DATE_FORMAT_NO_YEAR = 'D MMM';

const TIME_FORMAT = 'h:mma';
export const MIDNIGHT = 'midnight';
export const MIDDAY = 'midday';

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
    return `${dayjsFormatDate(value, SHORT_SPACE_DATE_FORMAT)}, ${time}`;
  }
}

/***
 * Pipe for formatting date and time in the user's timezone using the configured dayjs locale when space is an issue and according to GDS specifications
 * The main format is the 'D MMM YYYY, h:mma'
 * If the time is 00:00 then the format is 'D MMM YYYY, \'midnight\''
 * If the time is 12:00 then the format is 'D MMM YYYY, \'midday\''
 * Examples:
 * 12 November 2020 at 20:00 is formatted to 12 Nov 2020, 8:00pm
 * 12 November 2020 at 08:00 is formatted to 12 Nov 2020, 8:00am
 * 12 November 2020 at 00:00 is formatted to 12 Nov 2020, midnight
 * 12 November 2020 at 12:00 is formatted to 12 Nov 2020, midday
 */
@Pipe({ name: 'gdsDateTimeShortLocal' })
export class GdsDateTimeShortLocalPipe implements PipeTransform {
  constructor(
    @Inject(UK_ETS_DEFAULT_LOCALE) private readonly locale: string,
    @Inject(TIMEZONE) private readonly preferredTimezone: string
  ) {}

  /**
   * Transforms a date value to a short date and time format according to GDS specifications.
   * This implementation uses the user's timezone and the configured dayjs locale to format the date and time.
   * It does not use the Angular DatePipe to format the date and time,
   * but instead uses the dayjs library to format the date and time.
   *
   * @param value the date to format, we always assume that the value is in UTC and we will convert it to the user's timezone
   * @returns the formatted string value
   */
  transform(value: any): string | null {
    if (!value) {
      return null;
    }

    let date = dayjs.utc(value).locale(this.locale);

    try {
      date = date.tz(this.preferredTimezone).locale(this.locale);
    } catch {
      // Fallback to local parsing/formatting if timezone conversion is not supported.
    }

    if (!date.isValid()) {
      return null;
    }

    let time = date.format(TIME_FORMAT).toLowerCase();

    if (date.hour() === 0 && date.minute() === 0) {
      time = MIDNIGHT;
    } else if (date.hour() === 12 && date.minute() === 0) {
      time = MIDDAY;
    }

    return `${date.format(SHORT_SPACE_DATE_FORMAT)}, ${time}`;
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
export class GdsDateTimePipe implements PipeTransform {
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
    return `${dayjsFormatDate(value, FULL_SPACE_DATE_FORMAT)}, ${time}`;
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
    return dayjsFormatDate(value, SHORT_SPACE_DATE_FORMAT);
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
    return dayjsFormatDate(value, FULL_SPACE_DATE_FORMAT);
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
    return dayjsFormatDate(value, SHORT_SPACE_DATE_FORMAT_NO_YEAR);
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
    return `${dayjsFormatDate(
      value,
      FULL_SPACE_DATE_FORMAT,
      true
    )}, ${time} UTC`;
  }
}
