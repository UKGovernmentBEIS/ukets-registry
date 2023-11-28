import {
  AllocationCategoryLabel,
  AllocationCategory,
} from '@registry-web/shared/model/allocation';
import { AccountTypeMap } from '@shared/model/account';

export const TASK_STATUS_OPTIONS = [
  {
    label: '',
    value: null,
  },
  {
    label: 'All except completed',
    value: 'OPEN',
  },
  {
    label: 'Unclaimed',
    value: 'UNCLAIMED',
  },
  {
    label: 'Claimed',
    value: 'CLAIMED',
  },
  {
    label: 'Completed',
    value: 'COMPLETED',
  },
];

export const TASK_OUTCOME_OPTIONS = [
  {
    label: '',
    value: null,
  },
  {
    label: 'Approved',
    value: 'APPROVED',
  },
  {
    label: 'Rejected',
    value: 'REJECTED',
  },
];

interface AccountTypeOption {
  label: string;
  value: string;
  isKyoto?: boolean;
}

/**
 * Sorts Account Types putting ETS types first.
 */
function sortByKyoto(t1: AccountTypeOption, t2: AccountTypeOption) {
  if (t1.isKyoto === t2.isKyoto) {
    return 0;
  } else {
    if (t1.isKyoto) {
      return 1;
    } else {
      return -1;
    }
  }
}

/**
 * Sorts Account Types by label.
 */
function sortByLabel(t1: AccountTypeOption, t2: AccountTypeOption) {
  return t1.label.localeCompare(t2.label);
}

function addRecordValuesToAccountTypeOptionsArray() {
  return Object.entries(AccountTypeMap)
    .map(([type, typeValue]) => ({
      label: typeValue.label,
      value: type,
      isKyoto: typeValue.isKyoto,
    }))
    .sort((t1, t2) => {
      return sortByKyoto(t1, t2) || sortByLabel(t1, t2); // chained sorting, first sort by kyoto then by label
    });
}

export const ACCOUNT_TYPE_OPTIONS: AccountTypeOption[] = [
  {
    label: '',
    value: null,
  },
].concat(addRecordValuesToAccountTypeOptionsArray());

export const USER_TASK_OPTIONS = [
  {
    label: '',
    value: null,
  },
  {
    label: 'Yes',
    value: true,
  },
  {
    label: 'No',
    value: false,
  },
];

export const USER_ROLE_OPTIONS = [
  {
    label: '',
    value: null,
  },
  {
    label: 'All except Authorised Representatives',
    value: 'ALL_EXCEPT_AUTHORIZED_REPRESENTATIVES',
  },
  {
    label: 'Authorised Representative',
    value: 'AUTHORISED_REPRESENTATIVE',
  },
  {
    label: 'Senior registry administrator',
    value: 'SENIOR_REGISTRY_ADMINISTRATOR',
  },
  {
    label: 'Junior registry administrator',
    value: 'JUNIOR_REGISTRY_ADMINISTRATOR',
  },
  {
    label: 'Read-only registry administrator',
    value: 'READONLY_ADMINISTRATOR',
  },
];

export const ALLOCATION_CATEGORY_OPTIONS = [
  {
    label: '',
    value: null,
  },
  {
    label: AllocationCategoryLabel[AllocationCategory.Installation],
    value: AllocationCategory.Installation,
  },
  {
    label: AllocationCategoryLabel[AllocationCategory.AircraftOperator],
    value: AllocationCategory.AircraftOperator,
  },
];
