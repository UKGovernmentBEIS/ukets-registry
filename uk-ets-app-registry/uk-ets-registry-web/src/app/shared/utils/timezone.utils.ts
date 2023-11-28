import dayjs from 'dayjs';
import timezone from 'dayjs/plugin/timezone';

dayjs.extend(timezone);

export class TimezoneUtils {
  static calculateTimeZone(dateTime: string, isUTC: boolean) {
    // Get Local Difference in minutes from UTC
    const utcTimezoneOffsetMin = new Date().getTimezoneOffset();
    let utcDateTime;
    if (isUTC) {
      utcDateTime = dayjs(dateTime).add(utcTimezoneOffsetMin, 'm').toDate();
    } else {
      utcDateTime = dayjs(dateTime)
        .subtract(utcTimezoneOffsetMin, 'm')
        .toDate();
    }
    const dateTimeObj = dayjs(utcDateTime)
      .format('YYYY-MM-DD HH:mm:ss')
      .toString()
      .split(' ');
    return {
      date: dateTimeObj[0],
      time: dateTimeObj[1],
    };
  }
}

export interface DateTime {
  date: string;
  time: string;
}
