import {
  Option,
  SelectableOption,
} from '@shared/form-controls/uk-select-input/uk-select.model';
import { ReportType } from '@reports/model';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import { daysInMonth } from '@shared/shared.util';

export interface Section {
  id: number;
  displayOrder: number;
  title: string;
  summary: string;
  reportType: ReportType;
  displayType: DisplayType;
  lastUpdated?: string;
  publicationFrequency?: PublicationFrequency;
  publicationStart?: string;
  publicationTime?: string;
  publicationStartDateTime?: string;
  everyXDays?: number;
  generationDate?: string;
  generationTime?: string;
  lastReportPublished?: string;
}

export interface PublicationHistory {
  id: number;
  fileName: string;
  applicableForYear: number;
  publishedOn: string;
}

export enum DisplayType {
  ONE_FILE = 'ONE_FILE',
  ONE_FILE_PER_YEAR = 'ONE_FILE_PER_YEAR',
  MANY_FILES = 'MANY_FILES',
}

export enum PublicationFrequency {
  DAILY = 'DAILY',
  YEARLY = 'YEARLY',
  EVERY_X_DAYS = 'EVERY_X_DAYS',
  DISABLED = 'DISABLED',
}

export enum SectionType {
  KP = 'KP',
  ETS = 'ETS',
}

export interface MonthProperties {
  monthLabel: string;
  literal: number;
  daysOfMonth: number;
}

export const DisplayTypeMap: Record<DisplayType, string> = {
  [DisplayType.ONE_FILE]: 'Single file',
  [DisplayType.ONE_FILE_PER_YEAR]: 'One file per year',
  [DisplayType.MANY_FILES]: 'All files',
};

export const PublicationFrequencyMap: Record<PublicationFrequency, string> = {
  [PublicationFrequency.DAILY]: 'Daily',
  [PublicationFrequency.YEARLY]: 'Yearly',
  [PublicationFrequency.EVERY_X_DAYS]: 'Every specified day',
  [PublicationFrequency.DISABLED]: 'Disabled',
};

export const MonthPropertiesMap: Record<string, MonthProperties> = {
  January: {
    monthLabel: 'January',
    daysOfMonth: daysInMonth(1),
    literal: 1,
  },
  February: {
    monthLabel: 'February',
    daysOfMonth: daysInMonth(2),
    literal: 2,
  },
  March: {
    monthLabel: 'March',
    daysOfMonth: daysInMonth(3),
    literal: 3,
  },
  April: {
    monthLabel: 'April',
    daysOfMonth: daysInMonth(4),
    literal: 4,
  },
  May: {
    monthLabel: 'May',
    daysOfMonth: daysInMonth(5),
    literal: 5,
  },
  June: {
    monthLabel: 'June',
    daysOfMonth: daysInMonth(6),
    literal: 6,
  },
  July: {
    monthLabel: 'July',
    daysOfMonth: daysInMonth(7),
    literal: 7,
  },
  August: {
    monthLabel: 'August',
    daysOfMonth: daysInMonth(8),
    literal: 8,
  },
  September: {
    monthLabel: 'September',
    daysOfMonth: daysInMonth(9),
    literal: 9,
  },
  October: {
    monthLabel: 'October',
    daysOfMonth: daysInMonth(10),
    literal: 10,
  },
  November: {
    monthLabel: 'November',
    daysOfMonth: daysInMonth(11),
    literal: 11,
  },
  December: {
    monthLabel: 'December',
    daysOfMonth: daysInMonth(12),
    literal: 12,
  },
};

export const MonthOptions: SelectableOption[] = [
  { label: 'January', value: 1 },
  { label: 'February', value: 2 },
  { label: 'March', value: 3 },
  { label: 'April', value: 4 },
  { label: 'May', value: 5 },
  { label: 'June', value: 6 },
  { label: 'July', value: 7 },
  { label: 'August', value: 8 },
  { label: 'September', value: 9 },
  { label: 'October', value: 10 },
  { label: 'November', value: 11 },
  { label: 'December', value: 12 },
];

export interface SortActionPayload {
  sortParameters: SortParameters;
  potentialErrors: Map<any, ErrorDetail>;
}
