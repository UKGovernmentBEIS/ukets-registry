/* eslint-disable ngrx/prefer-inline-action-props */
import { createAction, props } from '@ngrx/store';
import {
  PaymentSearchCriteria,
  PaymentSearchResult,
} from '@payment-management/model';
import { SearchActionPayload } from '@shared/search/util/search-service.util';
import { Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';

export const loadPayments = createAction(
  '[Payment list Effect] Load the Payments and pagination',
  props<SearchActionPayload<PaymentSearchCriteria>>()
);

export const searchPayments = createAction(
  '[Payment List: Search form: Search button] Search for results',
  props<SearchActionPayload<PaymentSearchCriteria>>()
);

export const navigateToPageOfResults = createAction(
  '[Payment List Paginator] Select the page of results',
  props<SearchActionPayload<PaymentSearchCriteria>>()
);

export const replaySearch = createAction(
  '[Payment List resolver] Replay the search by using the stored criteria, page number, page size (LOAD mode)',
  props<SearchActionPayload<PaymentSearchCriteria>>()
);

export const navigateToNextPageOfResults = createAction(
  '[Payment List Paginator] Select the next page of results',
  props<SearchActionPayload<PaymentSearchCriteria>>()
);

export const navigateToPreviousPageOfResults = createAction(
  '[Payment List Paginator] Select the previous page of results',
  props<SearchActionPayload<PaymentSearchCriteria>>()
);

export const navigateToLastPageOfResults = createAction(
  '[Payment List Paginator] Select the last page of results',
  props<SearchActionPayload<PaymentSearchCriteria>>()
);

export const navigateToFirstPageOfResults = createAction(
  '[Payment List Paginator] Select the first page of results',
  props<SearchActionPayload<PaymentSearchCriteria>>()
);

export const changePageSize = createAction(
  '[Payment List Paginator] Change the page size of results',
  props<SearchActionPayload<PaymentSearchCriteria>>()
);

export const sortResults = createAction(
  '[Payment List results sort column] Sort results by column',
  props<SearchActionPayload<PaymentSearchCriteria>>()
);

export const paymentsLoaded = createAction(
  '[Payment list Effect] Payment results and pagination info loaded',
  props<{
    results: PaymentSearchResult[];
    pagination: Pagination;
    sortParameters: SortParameters;
    criteria: PaymentSearchCriteria;
  }>()
);

export const hideCriteria = createAction(
  '[Payment List show/hide button] Set the hideCriteria boolean to false'
);

export const showCriteria = createAction(
  '[Payment List show/hide button] Set the hideCriteria boolean to true'
);

export const showAdvancedSearch = createAction(
  '[Payment List advanced search button] Set the advanced search visible or hidden',
  props<{ isVisible: boolean }>()
);

export const clearState = createAction(
  '[Payment list resolver] Clear the state of Payment List (default mode)'
);

export const resetResultsLoaded = createAction(
  '[Payment list resolver] Set the resultsLoaded flag to false (LOAD mode)'
);

export const resetSuccess = createAction(
  '[Payment list resolver] Send command to clear the success message'
);

export const clearSuccess = createAction(
  '[Payment list Effect] Clear the success message'
);

export const setReferenceNumber = createAction(
  '[Payment list Effect] Set referenceNumber',
  props<{
    referenceNumber: number;
  }>()
);

//Navigation actions
export const navigateAndLoadPaymentList = createAction(
  '[Payment list Navigation Effect] Navigate load the Payments and pagination',
  props<{
    referenceNumber: number;
  }>()
);
