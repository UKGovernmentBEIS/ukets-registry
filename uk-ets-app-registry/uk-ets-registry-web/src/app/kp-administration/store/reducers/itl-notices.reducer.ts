import { Action, createReducer, on } from '@ngrx/store';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Notice } from '@kp-administration/itl-notices/model';
import { getNoticeSuccess, noticesLoaded, resetState } from '../actions';
import { mutableOn } from '@shared/mutable-on';

export interface NoticesState {
  pagination: Pagination;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  notices: Notice[];
  notice: Notice[];
}

export const initialState: NoticesState = {
  notices: undefined,
  notice: undefined,
  pagination: undefined,
  pageParameters: { pageSize: 10, page: 0 },
  sortParameters: { sortDirection: 'ASC', sortField: 'notificationIdentifier' }
};

export const itlNoticesReducer = createReducer(
  initialState,
  mutableOn(noticesLoaded, (state, { notices, pagination, sortParameters }) => {
    state.notices = notices;
    state.pagination = pagination;
    state.pageParameters = {
      page: pagination.currentPage - 1,
      pageSize: pagination.pageSize
    };
    state.sortParameters = sortParameters;
  }),
  mutableOn(getNoticeSuccess, (state, { notice }) => {
    state.notice = notice;
  }),
  on(resetState, (state): NoticesState => initialState)
);

export function reducer(state: NoticesState | undefined, action: Action) {
  return itlNoticesReducer(state, action);
}
