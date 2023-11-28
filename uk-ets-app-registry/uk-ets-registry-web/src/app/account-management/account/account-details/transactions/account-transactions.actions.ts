import { createAction, props } from '@ngrx/store';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { PagedResults } from '@shared/search/util/search-service.util';
import { SearchActionPayload } from '@account-management/account/account-details/transactions/transaction-list.model';
import { Transaction } from '@shared/model/transaction';

export const fetchAccountTransactions = createAction(
  '[Account Transactions] Fetch account transactions',
  props<{
    fullIdentifier: string;
    pageParameters: PageParameters;
    sortParameters: SortParameters;
    loadPageParametersFromState?: boolean;
  }>()
);

export const fetchAccountTransactionsReport = createAction(
  '[Account Transactions] Fetch account transactions report',
  props<{
    fullIdentifier: string;
  }>()
);

export const loadAccountTransactions = createAction(
  '[Account Transactions] Load account transactions',
  props<{
    transactions: PagedResults<Transaction>;
    pageParameters: PageParameters;
  }>()
);

export const sortResults = createAction(
  '[Account Transactions] Sort results by column',
  props<SearchActionPayload>()
);

export const navigateToNextPageOfResults = createAction(
  '[Account Transactions] Select the next page of results',
  props<SearchActionPayload>()
);

export const navigateToPreviousPageOfResults = createAction(
  '[Account Transactions] Select the previous page of results',
  props<SearchActionPayload>()
);

export const navigateToLastPageOfResults = createAction(
  '[Account Transactions] Select the last page of results',
  props<SearchActionPayload>()
);

export const navigateToFirstPageOfResults = createAction(
  '[Account Transactions] Select the first page of results',
  props<SearchActionPayload>()
);

export const changePageSize = createAction(
  '[Account Transactions] Change the page size of results',
  props<SearchActionPayload>()
);
