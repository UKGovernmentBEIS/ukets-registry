import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { ErrorDetail } from '@shared/error-summary';
import { TransactionSearchCriteria } from '@shared/model/transaction/transaction-list';

export interface FiltersDescriptor {
  accountTypeOptions: Option[];
  transactionTypeOptions: Option[];
}

export interface SearchActionPayload {
  criteria: TransactionSearchCriteria;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  potentialErrors: Map<any, ErrorDetail>;
  isReport?: boolean;
  loadPageParametersFromState?: boolean;
}
