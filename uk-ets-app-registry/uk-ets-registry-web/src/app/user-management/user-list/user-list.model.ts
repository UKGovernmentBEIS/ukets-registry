import { PageParameters } from '@shared/search/paginator/paginator.model';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { ErrorDetail } from '@shared/error-summary/error-detail';

export interface UserSearchCriteria {
  nameOrUserId: string;
  status: string;
  email: string;
  lastSignInFrom: string;
  lastSignInTo: string;
  role: string;
}

export interface UserProjection {
  userId: string;
  firstName: string;
  lastName: string;
  registeredOnDate: string;
  lastSignInDate: string;
  status: string;
  knownAs: string;
}

export interface SearchActionPayload {
  criteria: UserSearchCriteria;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  potentialErrors: Map<any, ErrorDetail>;
  loadPageParametersFromState?: boolean;
}

export const USER_STATUS_OPTIONS = [
  {
    label: '',
    value: null,
  },
  {
    label: 'All except DEACTIVATED',
    value: 'ALL_EXCEPT_DEACTIVATED',
  },
  {
    label: 'REGISTERED',
    value: 'REGISTERED',
  },
  {
    label: 'VALIDATED',
    value: 'VALIDATED',
  },
  {
    label: 'ENROLLED',
    value: 'ENROLLED',
  },
  {
    label: 'SUSPENDED',
    value: 'SUSPENDED',
  },
  {
    label: 'DEACTIVATION PENDING',
    value: 'DEACTIVATION_PENDING',
  },
  {
    label: 'DEACTIVATED',
    value: 'DEACTIVATED',
  },
];

export const USER_ROLE_OPTIONS = [
  {
    label: '',
    value: null,
  },
  {
    label: 'Limited-access User',
    value: 'USER',
  },
  {
    label: 'Authorised Representatives',
    value: 'AUTHORISED_REPRESENTATIVE',
  },
  {
    label: 'Authority',
    value: 'AUTHORITY_USER',
  },
  {
    label: 'Senior registry administrator',
    value: 'SENIOR_ADMIN',
  },
  {
    label: 'Junior registry administrator',
    value: 'JUNIOR_ADMIN',
  },
  {
    label: 'Read only registry administrator',
    value: 'READONLY_ADMIN',
  },
  {
    label: 'System Administrator',
    value: 'SYSTEM_ADMINISTRATOR',
  },
];
