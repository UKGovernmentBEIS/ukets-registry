import { AbstractControl } from '@angular/forms';
import dayjs, { Dayjs } from 'dayjs';
import advancedFormat from 'dayjs/plugin/advancedFormat';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import utc from 'dayjs/plugin/utc';

import { UkDate } from './model/uk-date';
import { IUkOfficialCountry } from './countries/country.interface';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { Status } from '@shared/model/status';
import { AccountHolderType } from '@shared/model/account';
import { AccountHolderTypeAheadSearchResult } from '@registry-web/account-shared/model';
import { Notification } from '@notifications/notifications-wizard/model';
import { DatePipe } from '@angular/common';
import { dateInvalidAt } from './utils/date.util';

dayjs.extend(customParseFormat);
dayjs.extend(advancedFormat);
dayjs.extend(utc);

const DEFAULT_LOCALE = 'en-US';

export const NOW = 'NOW';

export function timeNow(): string {
  try {
    return new DatePipe(DEFAULT_LOCALE).transform(new Date(), 'HH:mm:ss');
  } catch (e) {
    console.warn('timeNow ERROR:', e);
    return null;
  }
}

export function ukDateInvalidAt(date: UkDate) {
  return dateInvalidAt(Number(date.day), Number(date.month), Number(date.year));
}

export function getDayjs(date: UkDate, format?: string | string[]): Dayjs {
  if (!format) format = ['M/D/YYYY', 'MM/DD/YYYY', 'MM/D/YYYY', 'M/DD/YYYY'];
  const normalisedYear = dayjs().year(Number(date.year)).format('YYYY');
  return dayjs(
    date.month + '/' + date.day + '/' + normalisedYear,
    format,
    true
  );
}

export function dayjsFormatDate(
  date: Date,
  format: string,
  isUTC = false
): string {
  return isUTC ? dayjs.utc(date).format(format) : dayjs(date).format(format);
}

export function dateTimeNowFormatted() {
  return dayjs().format('dddd, MMMM Do YYYY, h:mm:ss a');
}

export function diffInYears(date: Dayjs): number {
  return dayjs().diff(date, 'year');
}

export function concatDateTime(date: string, time: string) {
  const time24 = dayjs(time, [
    'h:ma',
    'hh:mma',
    'h:mma',
    'hh:ma',
    'h:m',
    'hh:mm',
    'h:mm',
    'hh:m',
  ]).format('HH:mm');
  const utc = dayjs.utc(date + ' ' + time24, 'DD MMM YYYY HH:mm').toDate();
  return utc.toString();
}

export function convertToUkDate(formattedUkDate: string): UkDate {
  const date = dayjs(
    formattedUkDate,
    ['DD/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY', 'D/MM/YYYY'],
    true
  );
  return {
    day: String(date.date()),
    month: String(date.month() + 1),
    year: String(date.year()),
  };
}

export function convertDateForDatepicker(date: Date): string {
  return dayjs(date).format('YYYY-MM-DD');
}

export function convertDateToUkDate(date: Date): UkDate {
  date = new Date(date);
  return {
    day: String(date.getDate()),
    month: String(date.getMonth() + 1),
    year: String(date.getFullYear()),
  };
}

export function convertDatestringToUkDate(
  formattedUkDate: string,
  format: string
): UkDate {
  const date = dayjs(formattedUkDate, format, true);
  return {
    day: String(date.date()),
    month: String(date.month() + 1),
    year: String(date.year()),
  };
}

export function dateIsValid(date: string, format: string): boolean {
  return dayjs(date, format, true).isValid();
}

export function emptyProp(value: any): boolean {
  for (const prop in value) {
    if (empty(value[prop])) {
      return true;
    }
  }
  return false;
}

export function empty(o: any): boolean {
  return typeof o === 'undefined' || o === null || o === '';
}

export function isEmptyInputValue(value: any): boolean {
  /**
   * Check if the object is a string or array before evaluating the length attribute.
   * This avoids falsely rejecting objects that contain a custom length attribute.
   * For example, the object {id: 1, length: 0, width: 0} should not be returned as empty.
   */
  return (
    value === null ||
    value === undefined ||
    ((typeof value === 'string' || Array.isArray(value)) && value.length === 0)
  );
}

export function getControlName(c: AbstractControl): string | null {
  const formGroup = c.parent.controls;
  return Object.keys(formGroup).find((name) => c === formGroup[name]) || null;
}

export function leftPadZeros(value: string, length: number) {
  if (!value || value.length >= length) {
    return value;
  }
  let leadingZeros = '';
  for (let i = 0; i < length - value.length; i++) {
    leadingZeros += '0';
  }

  return `${leadingZeros}${value}`;
}

export function getLabel(
  key: string,
  options: {
    label: string;
    value: string;
  }[]
): string {
  if (!options || !key) {
    return null;
  }
  const tasks = options.filter((task) => task.value === key);
  return tasks.length ? tasks[0].label : key;
}

export function getConfigurationValue(
  key: string,
  options: { [property: string]: any }[]
) {
  const test = options.filter((obj) => obj[key] !== undefined);
  return test[0][key];
}

export function convertStringToJson(value: string) {
  if (!value) {
    return null;
  }
  return JSON.parse(value.replace(/'/g, '"'));
}

export function getCountryNameFromCountryCode(
  countries: IUkOfficialCountry[],
  key: string
) {
  const getDefaultValue = (key: string) => (key === '-' ? 'Not provided' : key);
  const iUkOfficialCountry: IUkOfficialCountry = countries.find(
    (country) => country.key === key
  );
  return iUkOfficialCountry
    ? iUkOfficialCountry.item[0]?.name
    : getDefaultValue(key);
}

export function getOptionsFromStatusMap(map: Record<any, Status>): Option[] {
  const options = Object.entries(map).map(([value, status]: [any, Status]) => ({
    label: status.label,
    value,
  }));

  options.unshift({ label: '', value: null });
  return options;
}

export function getOptionsFromMap(map: Record<any, string>): Option[] {
  const options = Object.entries(map).map(([value, label]: [any, string]) => ({
    label,
    value,
  }));

  options.unshift({ label: '', value: null });
  return options;
}

export function getUrlIdentifier(parameter: string): string {
  const sanitizedParameter = parameter.replace('#main-content', '');
  return sanitizedParameter.indexOf('?') > -1
    ? sanitizedParameter.split('?')[0]
    : sanitizedParameter.indexOf('%') > -1
      ? sanitizedParameter.split('%')[0]
      : sanitizedParameter;
}

export function accountHolderResultFormatter(
  item: AccountHolderTypeAheadSearchResult
): string {
  if (item.type === AccountHolderType.INDIVIDUAL && item.lastName) {
    return `${item.firstName} ${item.lastName}`;
  }
  return item.name;
}

export function keepSingleSpace(value: string) {
  return empty(value) ? value : value.trim().replace(/\s+/g, ' ');
}

export function generateHoursOptions() {
  const scheduledTimeOptions = [
    {
      label: '',
      value: null,
    },
  ];
  for (let index = 0; index < 12; index++) {
    scheduledTimeOptions.push({
      label: (index == 0 ? '12' : index) + ':00am',
      value: (index < 10 ? '0' + index : index) + ':00:00',
    });
    scheduledTimeOptions.push({
      label: (index == 0 ? '12' : index) + ':30am',
      value: (index < 10 ? '0' + index : index) + ':30:00',
    });
  }
  for (let index = 0; index < 12; index++) {
    scheduledTimeOptions.push({
      label: (index == 0 ? '12' : index) + ':00pm',
      value: index + 12 + ':00:00',
    });
    scheduledTimeOptions.push({
      label: (index == 0 ? '12' : index) + ':30pm',
      value: index + 12 + ':30:00',
    });
  }
  return scheduledTimeOptions;
}

export function generateHoursOptionsWithNow() {
  const scheduledTimeOptions = [
    {
      label: '',
      value: null,
    },
    {
      label: 'now',
      value: NOW,
    },
  ];
  for (let index = 0; index < 12; index++) {
    scheduledTimeOptions.push({
      label: (index == 0 ? '12' : index) + ':00am',
      value: (index < 10 ? '0' + index : index) + ':00:00',
    });
    scheduledTimeOptions.push({
      label: (index == 0 ? '12' : index) + ':30am',
      value: (index < 10 ? '0' + index : index) + ':30:00',
    });
  }
  for (let index = 0; index < 12; index++) {
    scheduledTimeOptions.push({
      label: (index == 0 ? '12' : index) + ':00pm',
      value: index + 12 + ':00:00',
    });
    scheduledTimeOptions.push({
      label: (index == 0 ? '12' : index) + ':30pm',
      value: index + 12 + ':30:00',
    });
  }
  return scheduledTimeOptions;
}

export function generateYearOptions(start: number): Option[] {
  const options: Option[] = [];
  const current = new Date().getFullYear();
  const length = current - start;

  if (length === 0) {
    options.push({ label: start.toString(), value: start });
  }

  if (length > 0) {
    for (let i = 0; i <= length; i++) {
      let year = start;
      year += i;
      options.push({ label: year.toString(), value: year });
    }
  }
  return options;
}

export function range(start, end) {
  return Array(end - start + 1)
    .fill(undefined)
    .map((_, idx) => start + idx);
}

export function daysInMonth(m: number, y?: number) {
  if (empty(y)) {
    y = new Date().getFullYear();
  }
  switch (m) {
    case 2:
      return (y % 4 == 0 && y % 100) || y % 400 == 0 ? 29 : 28;
    case 9:
    case 4:
    case 6:
    case 11:
      return 30;
    default:
      return 31;
  }
}

export function copy(object: any) {
  try {
    return JSON.parse(JSON.stringify(object));
  } catch (e) {
    console.warn(e);
    return object;
  }
}

/**
 * Create label for custom scheduled time that does not exist in the pregenerated options list
 * @param scheduledDate
 * @param scheduledTime
 * @returns
 */
export function getLabelForCustomNotificationTime(
  scheduledDate?: string,
  scheduledTime?: string
): string | undefined {
  const TIME_FORMAT = 'h:mma';
  if (scheduledDate && scheduledTime) {
    try {
      return new DatePipe(DEFAULT_LOCALE)
        .transform(scheduledDate + ' ' + scheduledTime, TIME_FORMAT)
        .toLowerCase();
    } catch (e) {
      console.warn('getLabelForCustomNotificationTime ERROR:', e);
      return null;
    }
  }
}

/**
 * If there is a custom option (eg entered from `now` option previously) in the scheduled time field,
 * and it does not already exist in the dropdown options,
 * add it so it can be visible in the select component
 * @param notification
 * @param options
 * @param sort
 * @returns
 */
export function handleCustomScheduledTimeOption(
  notification: Notification,
  options: Option[],
  sort = false
): Option[] {
  const hasScheduledTime = !!notification?.activationDetails?.scheduledTime;

  const existsInOptions = options.find(
    (o) => o.value === notification?.activationDetails?.scheduledTime
  );
  if (hasScheduledTime && !existsInOptions) {
    const label = getLabelForCustomNotificationTime(
      notification.activationDetails.scheduledDate,
      notification.activationDetails.scheduledTime
    );
    const value = notification.activationDetails.scheduledTime;
    options.push({ label, value });

    if (sort) options.sort((a, b) => compareTimeOptions(a, b, notification));
  }
  return options;
}

/**
 * Parse input as date and set hour, minute and second at the end of the given day
 *
 * @param date
 * @returns Date
 */
export function parseDeadline(date: Date | string | null): Date {
  const deadline = new Date(date);
  deadline.setHours(23, 59, 59);
  return deadline;
}

function compareTimeOptions(a: Option, b: Option, notification: Notification) {
  const date_a = new Date(
    notification?.activationDetails?.scheduledDate + ' ' + a.value
  );
  const date_b = new Date(
    notification?.activationDetails?.scheduledDate + ' ' + b.value
  );
  if (isNaN(date_a.getTime()) || isNaN(date_b.getTime())) return 1;
  return new Date(date_a) > new Date(date_b) ? 1 : -1;
}
