import { Action, createReducer, on } from '@ngrx/store';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { mutableOn } from '@shared/mutable-on';

import {
  clearState,
  hideCriteria,
  messagesLoaded,
  resetResultsLoaded,
  showCriteria
} from '../actions/itl-messages.actions';
import {
  MessageSearchCriteria,
  MessageSearchResult
} from '@kp-administration/itl-messages/model';

export interface MessageListState {
  externalCriteria: boolean;
  pageParameters: PageParameters;
  pagination: Pagination;
  sortParameters: SortParameters;
  results: MessageSearchResult[];
  criteria: MessageSearchCriteria;
  hideCriteria: boolean;
  hideTable: boolean;
  resultsLoaded: boolean;
}

export const initialState = {
  externalCriteria: undefined,
  pageParameters: {
    page: 0,
    pageSize: 10
  },
  pagination: undefined,
  sortParameters: {
    sortField: 'messageDate',
    sortDirection: 'DESC'
  },
  results: undefined,
  criteria: {
    messageId: undefined,
    messageDateFrom: undefined,
    messageDateTo: undefined
  },
  hideCriteria: false,
  hideTable: true,
  resultsLoaded: false
};

const itlMessagesReducer = createReducer(
  initialState,
  mutableOn(resetResultsLoaded, state => {
    state.resultsLoaded = false;
  }),
  mutableOn(
    messagesLoaded,
    (state, { results, pagination, sortParameters, criteria }) => {
      state.criteria = criteria;
      state.pagination = pagination;
      state.results = results;
      state.hideCriteria = true;
      state.hideTable = false;
      state.resultsLoaded = true;
      state.pageParameters = {
        page: pagination.currentPage - 1,
        pageSize: pagination.pageSize // The page size that user selected
      };
      state.sortParameters = sortParameters;
    }
  ),
  mutableOn(hideCriteria, state => {
    state.hideCriteria = true;
  }),
  mutableOn(showCriteria, state => {
    state.hideCriteria = false;
  }),
  on(clearState, (state): MessageListState => initialState)
);

export function reducer(state: MessageListState | undefined, action: Action) {
  return itlMessagesReducer(state, action);
}
