import { ErrorDetail } from '@registry-web/shared/error-summary';
import { PageParameters } from '@registry-web/shared/search/paginator';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import {
  AllocationJobSearchCriteria,
  AllocationJobStatus,
} from './allocation-job-search-criteria.model';
import { OperatorType } from '@registry-web/shared/model/account';

export type AllocationJob = {
  id: number;
  requestIdentifier: number;
  category: OperatorType;
  year: number;
  status: AllocationJobStatus;
  executionDate: Date;
};

export type SearchAllocationJobActionPayload = {
  criteria: AllocationJobSearchCriteria;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  potentialErrors: Map<any, ErrorDetail>;
  isReport?: boolean;
  loadPageParametersFromState?: boolean;
};
