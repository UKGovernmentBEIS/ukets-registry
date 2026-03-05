import { createFeature, createReducer, on } from '@ngrx/store';
import {
  RegulatorNoticeTask,
  RegulatorNoticeSearchCriteria,
} from '@shared/task-and-regulator-notice-management/model';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { mutableOn } from '@shared/mutable-on';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import { RegulatorNoticeListActions } from '@regulator-notice-management/list/store/regulator-notice-list.actions';
import { indexOfItem } from '@shared/task-and-regulator-notice-management/util';

const regulatorNoticeListFeatureKey = 'regulatorNoticeList';

export interface RegulatorNoticeListState {
  externalCriteria: boolean;
  pageParameters: PageParameters;
  pagination: Pagination;
  sortParameters: SortParameters;
  results: RegulatorNoticeTask[];
  criteria: RegulatorNoticeSearchCriteria;
  hideFilters: boolean;
  resultsLoaded: boolean;
  selectedRegulatorNotices: RegulatorNoticeTask[];
  processTypesList: string[];
}

const initialState: RegulatorNoticeListState = {
  externalCriteria: undefined,
  pageParameters: {
    page: 0,
    pageSize: 10,
  },
  pagination: undefined,
  sortParameters: {
    sortField: 'initiatedDate',
    sortDirection: 'DESC',
  },
  results: undefined,
  criteria: undefined,
  hideFilters: true,
  resultsLoaded: false,
  selectedRegulatorNotices: [],
  processTypesList: [],
};

const regulatorNoticeListReducer = createReducer(
  initialState,
  mutableOn(RegulatorNoticeListActions.RESET_RESULTS_LOADED, (state) => {
    state.resultsLoaded = false;
  }),
  mutableOn(
    RegulatorNoticeListActions.SEARCH_RESULTS_PAGE_LOADED,
    (state, { results, pagination, sortParameters, criteria }) => {
      state.criteria = criteria;
      state.pagination = pagination;
      state.results = results;
      state.resultsLoaded = true;
      state.pageParameters = {
        page: pagination.currentPage - 1,
        pageSize: pagination.pageSize,
      };
      state.sortParameters = sortParameters;
    }
  ),
  mutableOn(RegulatorNoticeListActions.TOGGLE_FILTERS, (state) => {
    state.hideFilters = !state.hideFilters;
  }),
  on(
    RegulatorNoticeListActions.CLEAR_SEARCH_STATE,
    (state): RegulatorNoticeListState => ({
      ...initialState,
      processTypesList: state.processTypesList,
    })
  ),
  mutableOn(
    RegulatorNoticeListActions.UPDATE_SELECTED,
    (state, { added, removed }) => {
      state.selectedRegulatorNotices = state.selectedRegulatorNotices
        .slice()
        .filter((notice) => indexOfItem(removed, notice) < 0)
        .filter((notice) => indexOfItem(added, notice) < 0)
        .concat(added);
    }
  ),
  mutableOn(
    RegulatorNoticeListActions.SET_PROCESS_TYPES_LIST,
    (state, { processTypesList }) => {
      state.processTypesList = [...processTypesList].sort((a, b) =>
        a.localeCompare(b)
      );
    }
  ),
  mutableOn(RegulatorNoticeListActions.CLEAR_SELECTION, (state) => {
    state.selectedRegulatorNotices = [];
  })
);

export const regulatorNoticeListFeature = createFeature({
  name: regulatorNoticeListFeatureKey,
  reducer: regulatorNoticeListReducer,
});
