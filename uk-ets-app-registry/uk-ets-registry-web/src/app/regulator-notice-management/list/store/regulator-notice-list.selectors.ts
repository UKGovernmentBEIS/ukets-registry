import { regulatorNoticeListFeature } from '@regulator-notice-management/list/store/regulator-notice-list.reducer';

export const {
  selectRegulatorNoticeListState,
  selectExternalCriteria,
  selectPagination,
  selectPageParameters,
  selectSortParameters,
  selectResults,
  selectHideFilters,
  selectResultsLoaded,
  selectCriteria,
  selectSelectedRegulatorNotices,
  selectProcessTypesList,
} = regulatorNoticeListFeature;
