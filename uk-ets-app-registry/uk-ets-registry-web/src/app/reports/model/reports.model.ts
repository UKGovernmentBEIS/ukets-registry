import {
  Option,
  SelectableOption,
} from '@shared/form-controls/uk-select-input/uk-select.model';
import {
  CommitmentPeriod,
  COMMITMENT_PERIOD_LABELS,
} from '@shared/model/transaction';
import { generateHoursOptions, generateYearOptions } from '@shared/shared.util';
import { ValidatorFn, Validators } from '@angular/forms';
import { UkRegistryValidators } from '@shared/validation';
/**
 * Represents a Report as retrieved by the server.
 */

export interface Report {
  id: number;
  status: ReportStatus;
  type: ReportType;
  fullUserName: string;
  requestingUser: string;
  requestDate: string;
  generationDate?: string;
  expirationDate?: string;
  fileSize?: number;
  fileName?: string;
}

/**
 * Represents a request for a new report.
 */
export interface ReportCreationRequest {
  type: ReportType;
  requestingRole?: ReportRequestingRole;
  queryInfo?:
    | ReportYearCriteria
    | ReportDateRangeCriteria
    | SEFReportCriteria
    | CutOffDateTimeReportCriteria;
}

export interface ReportCreationResponse {
  reportId?;
  number?;
}

export enum ReportStatus {
  PENDING = 'PENDING',
  DONE = 'DONE',
  FAILED = 'FAILED',
}

export enum ReportType {
  R0001 = 'R0001',
  R0002 = 'R0002',
  R0003 = 'R0003',
  R0004 = 'R0004',
  R0005 = 'R0005',
  R0006 = 'R0006',
  R0007 = 'R0007',
  R0008 = 'R0008',
  R0009 = 'R0009',
  R0010 = 'R0010',
  R0011 = 'R0011',
  R0012 = 'R0012',
  R0013 = 'R0013',
  R0014 = 'R0014',
  R0015 = 'R0015',
  R0016 = 'R0016',
  R0017 = 'R0017',
  R0018 = 'R0018',
  R0019 = 'R0019',
  R0020 = 'R0020',
  R0021 = 'R0021',
  R0022 = 'R0022',
  R0023 = 'R0023',
  R0024 = 'R0024',
  R0025 = 'R0025',
  R0026 = 'R0026',
  R0027 = 'R0027',
  R0028 = 'R0028',
  R0029 = 'R0029',
  R0030 = 'R0030',
  R0031 = 'R0031',
  R0032 = 'R0032',
  R0033 = 'R0033',
  R0036 = 'R0036',
  R0037 = 'R0037',
  R0038 = 'R0038',
  R0039 = 'R0039',
  R0040 = 'R0040',
  R0041 = 'R0041',
  R0042 = 'R0042',
  R0043 = 'R0043',
  R0044 = 'R0044',
  R0045 = 'R0045',
  R0046 = 'R0046',
  R0047 = 'R0047',
  R0048 = 'R0048',
  R0049 = 'R0049',
  R0050 = 'R0050',
}

export const SEFYearOptions: Option[] = [
  { label: '2013', value: 2013 },
  { label: '2014', value: 2014 },
  { label: '2015', value: 2015 },
  { label: '2016', value: 2016 },
  { label: '2017', value: 2017 },
  { label: '2018', value: 2018 },
  { label: '2019', value: 2019 },
  { label: '2020', value: 2020 },
  { label: '2021', value: 2021 },
  { label: '2022', value: 2022 },
  { label: '2023', value: 2023 },
];

export const CommitmentPeriodOptions: SelectableOption[] = [
  {
    label: COMMITMENT_PERIOD_LABELS[CommitmentPeriod.CP1],
    value: '1',
  },
  {
    label: COMMITMENT_PERIOD_LABELS[CommitmentPeriod.CP2],
    value: '2',
    selected: true,
  },
];

export const RegulatorsOptions: Option[] = [
  { label: '', value: null },
  { label: 'EA', value: ['EA'] },
  { label: 'NRW', value: ['NRW'] },
  { label: 'SEPA', value: ['SEPA'] },
  { label: 'DAERA', value: ['DAERA'] },
  { label: 'OPRED', value: ['OPRED'] },
];

export enum ReportRequestingRole {
  ADMINISTRATOR = 'administrator',
  AUTHORITY = 'authority',
}

/**
 * TODO: decide how we will represent criteria in the client
 */
export interface ReportYearCriteria {
  year: number;
}

export interface ReportDateRangeCriteria {
  from: string;
  to: string;
}

export interface SEFReportCriteria {
  year: number;
  commitmentPeriod: number;
}

export interface CutOffDateTimeReportCriteria {
  to: string;
  cutOffTime: string;
}

export interface StandardReport {
  label: string;
  type: ReportType;
}

export interface FormModel {
  [key: string]: any;
}

export interface Filters {
  period: boolean;
  model: FormModel;
  inputType: 'datepicker' | 'select';
  optionsValues?: Option[];
  label?: string;
  hint?: string;
  isHorizontal?: boolean;
}

export interface ReportTypeValue {
  label: string;
  summary?: string;
  isStandard?: boolean;
  order?: number;
  filters?: Filters[];
  groupValidators?: ValidatorFn[];
}

export function generate050YearOptions(date: string) {
  const initialYear = 2025;
  const now = new Date();
  const finalYear = now.getFullYear();

  const [day, month] = date.split('/');
  const configurationDate = new Date(
    finalYear,
    parseInt(month, 10) - 1,
    parseInt(day, 10)
  );

  const options: Option[] = [];
  const length = finalYear - initialYear;
  if (length === 0) {
    if (configurationDate < now) {
      options.push({ label: initialYear.toString(), value: initialYear });
      reportTypeMap.R0050.filters[0].optionsValues = options;
    }
  }

  if (length > 0) {
    for (let i = 0; i <= length; i++) {
      let year = initialYear;
      year += i;
      if (i == length) {
        if (now > configurationDate) {
          options.push({ label: year.toString(), value: year });
        }
      } else {
        options.push({ label: year.toString(), value: year });
      }
    }
    reportTypeMap.R0050.filters[0].optionsValues = options;
  }
}

export const reportTypeMap: Record<ReportType, ReportTypeValue> = {
  [ReportType.R0001]: {
    label: 'Accounts with no ARs and no AR nominations',
    summary:
      'This report shows details of open accounts that do not have an AR or active AR nomination. It does not show Closed, Rejected or Proposed accounts.',
    isStandard: true,
    order: 2,
  },
  [ReportType.R0002]: {
    label: 'Users not linked to accounts ("orphans")',
    summary:
      'This report shows the User ID of all users that are not linked to an account. It does not show Deactivated users or users without a status.',
    isStandard: true,
    order: 19,
  },
  [ReportType.R0003]: {
    label: 'Authorised representatives per account',
    summary:
      'This report shows details for ARs linked to all accounts, including closed accounts. It does not show nominated ARs.',
    isStandard: true,
    order: 7,
  },
  [ReportType.R0004]: {
    label: 'All Users',
    summary:
      'This report shows details for all Registry users, including Deactivated ones.',
    isStandard: true,
    order: 3,
  },
  [ReportType.R0005]: {
    label: 'Search Tasks',
  },
  [ReportType.R0006]: {
    label: 'Search Transactions',
  },
  [ReportType.R0007]: {
    label: 'Search Accounts',
  },
  [ReportType.R0008]: {
    label: 'Accounts per AH',
    summary:
      'This report shows details of all accounts related to the selected user, including Closed, Suspended and Proposed accounts.',
    isStandard: true,
    order: 1,
  },
  [ReportType.R0009]: {
    label: 'Trusted accounts per account',
    summary:
      'This report includes the list of the trusted accounts (added manually or automatically) per account in the UK ETS registry.',
    isStandard: true,
    order: 14,
  },
  [ReportType.R0010]: {
    label: 'KP Report paragraph 48',
    summary:
      'This report shows all Account Holders of KP accounts holding ERUs, CERs, AAUs or RMUs units.',
    isStandard: true,
    order: 22,
  },
  [ReportType.R0011]: {
    label: 'KP Report paragraph 45',
    summary:
      'This report shows details for all KP accounts, including KP government accounts.',
    isStandard: true,
    order: 21,
  },
  [ReportType.R0012]: {
    label: 'UK ETS Trading Accounts',
    summary: 'This report shows details for all Trading Accounts.',
    isStandard: true,
    order: 17,
  },
  [ReportType.R0013]: {
    label: 'UK ETS Registry Participants and Allocations (OHA)',
    isStandard: true,
    summary:
      'This report shows details of all Account Holders on all OHA accounts, including those with zero allocations. It does not show details for accounts without an installation.',
    filters: [
      {
        period: false,
        model: { year: ['', Validators.required] },
        optionsValues: generateYearOptions(2021),
        inputType: 'select',
        label: 'Select year to generate the report',
      },
    ],
    order: 16,
  },
  [ReportType.R0014]: {
    label: 'UK ETS Registry Participants and Allocations (AOHA)',
    summary:
      'This report shows details of all Account Holders on all AOHA accounts, including those with zero allocations.',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { year: ['', Validators.required] },
        optionsValues: generateYearOptions(2021),
        inputType: 'select',
        label: 'Select year to generate the report',
      },
    ],
    order: 15,
  },
  [ReportType.R0015]: {
    label: 'Total Volume of Allowances within Registry',
    summary:
      'This report shows the total number of available and reserved allowances on all ETS accounts.',
    isStandard: true,
    order: 11,
  },
  [ReportType.R0016]: {
    label: 'Transaction - Balance Reports',
    summary:
      'This report shows the total number of available and reserved allowances on all ETS accounts',
    isStandard: true,
    groupValidators: [UkRegistryValidators.dateRangeValidator('from', 'to')],
    filters: [
      {
        period: true,
        model: { from: [null, UkRegistryValidators.dateFormatValidator()] },
        inputType: 'datepicker',
        isHorizontal: true,
      },
      {
        period: true,
        model: {
          to: [null, UkRegistryValidators.dateFormatValidator()],
        },
        inputType: 'datepicker',
        isHorizontal: true,
      },
    ],
    order: 12,
  },
  [ReportType.R0017]: {
    label: 'Transaction Volume & Number of Transactions',
    summary:
      'This report includes the total number of transactions, along with the corresponding number of units ' +
      'that were completed during the selected period from accounts of specific type to accounts of specific type.',
    isStandard: true,
    groupValidators: [UkRegistryValidators.dateRangeValidator('from', 'to')],
    filters: [
      {
        period: true,
        model: { from: [null, UkRegistryValidators.dateFormatValidator()] },
        inputType: 'datepicker',
        isHorizontal: true,
      },
      {
        period: true,
        model: {
          to: [null, UkRegistryValidators.dateFormatValidator()],
        },
        inputType: 'datepicker',
        isHorizontal: true,
      },
    ],
    order: 13,
  },
  [ReportType.R0018]: {
    label: ' AR tasks',
    summary:
      'This report shows show details of all pending Add AR tasks and Replace AR tasks.',
    isStandard: true,
    order: 6,
  },
  [ReportType.R0019]: {
    label: 'Verified emissions & Surrendered allowances',
    summary:
      'This report shows details of Installations and Aircraft Operators for all OHA and AOHA accounts. It does not show accounts without installations.',
    isStandard: true,
    order: 20,
  },
  [ReportType.R0020]: {
    label: 'Kyoto Protocol SEF RREG1',
    summary: 'Kyoto Protocol SEF Report.',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { commitmentPeriod: '' },
        optionsValues: CommitmentPeriodOptions,
        inputType: 'select',
        label: 'CP',
      },
      {
        period: false,
        model: { year: ['', Validators.required] },
        optionsValues: SEFYearOptions,
        inputType: 'select',
        label: 'Reported year',
      },
    ],
    order: 23,
  },
  [ReportType.R0021]: {
    label: 'Kyoto Protocol SEF RREG2',
    summary: 'Kyoto Protocol R2 Discrepant Transaction Report',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { year: ['', Validators.required] },
        optionsValues: SEFYearOptions,
        inputType: 'select',
        label: 'Reported year',
      },
    ],
    order: 24,
  },
  [ReportType.R0022]: {
    label: 'Kyoto Protocol SEF RREG3',
    summary: 'Kyoto Protocol R3 Notification List Report.',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { year: [null, Validators.required] },
        optionsValues: SEFYearOptions,
        inputType: 'select',
        label: 'Reported year',
      },
    ],
    order: 25,
  },
  [ReportType.R0023]: {
    label: 'Kyoto Protocol SEF RREG4',
    summary: 'Kyoto Protocol R4 Non-Replacements Report.',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { year: [null, Validators.required] },
        optionsValues: SEFYearOptions,
        inputType: 'select',
        label: 'Reported year',
      },
    ],
    order: 26,
  },
  [ReportType.R0024]: {
    label: 'Kyoto Protocol SEF RREG5',
    summary: 'Kyoto Protocol SEF R5 Invalid units Report',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { year: [null, Validators.required] },
        optionsValues: SEFYearOptions,
        inputType: 'select',
        label: 'Reported year',
      },
    ],
    order: 27,
  },
  [ReportType.R0025]: {
    label: 'Compliance 10 years',
    summary:
      'This report shows all OHA and AOHA accounts linked to a compliant Installation or Aircraft Operator.',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { to: [null, UkRegistryValidators.dateFormatValidator()] },
        inputType: 'datepicker',
        hint: 'This is the date on which we collect data.',
        isHorizontal: false,
        label: 'Set cut-off day',
      },
      {
        period: false,
        model: { cutOffTime: [null] },
        inputType: 'select',
        hint: 'This is the time at which we collect data. The time selected will be your local time.',
        isHorizontal: false,
        label: 'Set cut-off time',
        optionsValues: generateHoursOptions(),
      },
    ],
    order: 8,
  },
  [ReportType.R0026]: {
    label: 'Search Tasks',
  },
  [ReportType.R0027]: {
    label: 'Allocations report',
    summary:
      'This report shows details of the allocations for all Installations and Aircraft Operators by allocation table and year (including withheld status).',
    isStandard: true,
    order: 5,
  },
  [ReportType.R0028]: {
    label: 'Compliance verified emissions and surrenders',
    summary:
      'This report shows details regarding Emissions and Surrenders of all OHA and AOHA accounts.',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { to: [null, UkRegistryValidators.dateFormatValidator()] },
        inputType: 'datepicker',
        hint: 'This is the date on which we collect data.',
        isHorizontal: false,
        label: 'Set cut-off day',
      },
      {
        period: false,
        model: { cutOffTime: [null] },
        inputType: 'select',
        hint: 'This is the time at which we collect data. The time selected will be your local time.',
        isHorizontal: false,
        label: 'Set cut-off time',
        optionsValues: generateHoursOptions(),
      },
    ],
    order: 10,
  },
  [ReportType.R0029]: {
    label: 'Unit block report',
    summary:
      'This report shows details of the unit blocks split by ETS Phase or KP Commitment Period.',
    isStandard: true,
    order: 18,
  },
  [ReportType.R0030]: {
    label: 'Allocation preparation',
    summary:
      'This report shows details for the allocation preparation highlighting the outstanding actions remaining on the allocation table(s) to date.',
    isStandard: true,
    order: 4,
  },
  [ReportType.R0031]: {
    label: 'Compliance verified emissions',
    summary:
      'This report shows details regarding Verified Emissions of all OHA and AOHA accounts.',
    isStandard: true,
    order: 9,
    filters: [
      {
        period: false,
        model: { to: [null, UkRegistryValidators.dateFormatValidator()] },
        inputType: 'datepicker',
        hint: 'This is the date on which we collect data.',
        isHorizontal: false,
        label: 'Set cut-off day',
      },
      {
        period: false,
        model: { cutOffTime: [null] },
        inputType: 'select',
        hint: 'This is the time at which we collect data. The time selected will be your local time.',
        isHorizontal: false,
        label: 'Set cut-off time',
        optionsValues: generateHoursOptions(),
      },
    ],
  },
  [ReportType.R0032]: {
    label: 'Compliance verified emissions and surrenders',
    summary:
      'This report shows details regarding Emissions and Surrenders of all OHA and AOHA accounts.',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { to: [null, UkRegistryValidators.dateFormatValidator()] },
        inputType: 'datepicker',
        hint: 'This is the date on which we collect data.',
        isHorizontal: false,
        label: 'Set cut-off day',
      },
      {
        period: false,
        model: { cutOffTime: [null] },
        inputType: 'select',
        hint: 'This is the time at which we collect data. The time selected will be your local time.',
        isHorizontal: false,
        label: 'Set cut-off time',
        optionsValues: generateHoursOptions(),
      },
    ],
    order: 10,
  },
  [ReportType.R0033]: {
    label: 'Compliance verified emissions',
    summary:
      'This report shows details regarding Verified Emissions of all OHA and AOHA accounts.',
    isStandard: true,
    order: 9,
    filters: [
      {
        period: false,
        model: { to: [null, UkRegistryValidators.dateFormatValidator()] },
        inputType: 'datepicker',
        hint: 'This is the date on which we collect data.',
        isHorizontal: false,
        label: 'Set cut-off day',
      },
      {
        period: false,
        model: { cutOffTime: [null] },
        inputType: 'select',
        hint: 'This is the time at which we collect data. The time selected will be your local time.',
        isHorizontal: false,
        label: 'Set cut-off time',
        optionsValues: generateHoursOptions(),
      },
    ],
  },
  [ReportType.R0036]: {
    label: 'Submit Documents for User Report',
    summary:
      'This report shows the tasks of this type: [Submit documents for User] that have uploaded attachment(s) ' +
      'over 60 days ago, so the Registry Administrator can review and delete them manually.',
    isStandard: true,
    order: 36,
  },
  [ReportType.R0037]: {
    label: 'Submit documents for Account Holder',
    summary:
      'Submit documents for Account Holders that have uploaded attachment(s) over 60days ago, ' +
      'so the Registry Administrator can review and delete them manually.',
    isStandard: true,
    order: 37,
  },
  [ReportType.R0038]: {
    label: 'Allocation Transactions AU',
    summary: 'This report is related to the allocation transactions.',
    isStandard: true,
    order: 38,
  },
  [ReportType.R0039]: {
    label: 'Current Task List',
    summary:
      'This report shows details of all pending tasks that form the RA view of the task list in the Registry.',
    isStandard: true,
    order: 11,
  },
  [ReportType.R0040]: {
    label: 'Annual Task List',
    summary:
      'This report shows details of all completed tasks that form the RA view of the task list in the Registry.',
    isStandard: true,
    order: 40,
  },
  [ReportType.R0044]: {
    label: 'UK ETS Registry Participants (MOHA)',
    summary:
      'This report shows details of all Account Holders on all MOHA accounts.',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { year: ['', Validators.required] },
        optionsValues: generateYearOptions(2021),
        inputType: 'select',
        label: 'Select year to generate the report',
      },
    ],
    order: 41,
  },
  [ReportType.R0045]: {
    label: 'UK ETS Registry Participants (MOHA)',
    summary:
      'This report shows details of all Account Holders on all MOHA accounts.',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { year: ['', Validators.required] },
        optionsValues: generateYearOptions(2021),
        inputType: 'select',
        label: 'Select year to generate the report',
      },
    ],
    order: 42,
  },
  [ReportType.R0041]: {
    label: 'UK ETS Registry Participants and Allocations (OHA)',
    summary:
      'This report shows details of all Account Holders on all OHA accounts, including those with zero allocations for all allocation years. It does not show details for accounts without an installation.',
    isStandard: true,
    order: 16,
  },
  [ReportType.R0042]: {
    label: 'UK ETS Registry Participants and Allocations (AOHA)',
    summary:
      'This report shows details of all Account Holders on all AOHA accounts, including those with zero allocations for all allocation years.',
    isStandard: true,
    order: 15,
  },
  [ReportType.R0043]: {
    label: 'Allocation Returns',
    summary:
      'This report shows the allocated Allowances that have been returned through the completed transactions.',
    isStandard: true,
    groupValidators: [UkRegistryValidators.dateRangeValidator('from', 'to')],
    order: 5,
    filters: [
      {
        period: true,
        model: { from: [null, UkRegistryValidators.dateFormatValidator()] },
        inputType: 'datepicker',
        isHorizontal: true,
      },
      {
        period: true,
        model: {
          to: [null, UkRegistryValidators.dateFormatValidator()],
        },
        inputType: 'datepicker',
        isHorizontal: true,
      },
      {
        period: false,
        model: { regulators: [null] },
        optionsValues: RegulatorsOptions,
        inputType: 'select',
        label: 'Select Regulator',
      },
    ],
  },
  [ReportType.R0046]: {
    label: 'Notes of all Account Holders',
    summary: 'This report shows the Notes of all account holders.',
    isStandard: true,
    order: 43,
  },
  [ReportType.R0047]: {
    label: 'UKETS ARs one year since login',
    summary:
      'This report shows details for ARs linked to all accounts to identify those, who have not logged in for 12 months or more (i.e. more than 52 weeks).',
    isStandard: true,
    order: 44,
  },
  [ReportType.R0048]: {
    label: 'Compliance Management Report',
    summary: 'Compliance Management Report (CMR).',
  },
  [ReportType.R0049]: {
    label: 'Compliance data report',
    summary: 'Compliance data report',
    order: 45,
    isStandard: true,
  },
  [ReportType.R0050]: {
    label: 'Transaction List report (3 years old)',
    summary:
      'This report includes all transactions involving an Installation, Aviation or Maritime Operator or a Trader, that were completed during the reporting year that is 3 years before the one selected (e.g. the 2026 report includes transactions completed from 01 May 2022 and up to 30 Apr 2023).',
    isStandard: true,
    filters: [
      {
        period: false,
        model: { year: ['', Validators.required] },
        inputType: 'select',
        label: 'Select year to generate the report',
      },
    ],
    order: 46,
  },
};

export const DatePeriodOptions: Option[] = [
  { label: 'Custom date range', value: 'custom' },
];
