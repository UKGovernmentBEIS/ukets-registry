/* eslint-disable ngrx/prefer-inline-action-props */
import { createAction, props } from '@ngrx/store';
import {
  RegulatorNoticeSearchCriteria,
  RegulatorNoticeTask,
  SearchRegulatorNoticesActionPayload,
} from '@shared/task-and-regulator-notice-management/model/regulator-notice-list.model';
import { Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';

export const RegulatorNoticeListActions = {
  SET_PROCESS_TYPES_LIST: createAction(
    '[Regulator Notices List] Set Process Types List',
    props<{ processTypesList: string[] }>()
  ),
  LOAD_NOTICES: createAction(
    '[Regulator Notices List] Load the Regulator Notices and pagination',
    props<SearchRegulatorNoticesActionPayload>()
  ),
  SEARCH_REGULATOR_NOTICES: createAction(
    '[Regulator Notices List] Search for results',
    props<SearchRegulatorNoticesActionPayload>()
  ),
  SELECT_RESULTS_PAGE: createAction(
    '[Regulator Notices List] Select results page',
    props<SearchRegulatorNoticesActionPayload>()
  ),
  REPLAY_SEARCH: createAction(
    '[Regulator Notices List] Replay the search by using the stored criteria, page number, page size (LOAD mode)',
    props<SearchRegulatorNoticesActionPayload>()
  ),
  SELECT_NEXT_RESULTS_PAGE: createAction(
    '[Regulator Notices List] Select next results page',
    props<SearchRegulatorNoticesActionPayload>()
  ),
  SELECT_PREVIOUS_RESULTS_PAGE: createAction(
    '[Regulator Notices List] Select previous results page',
    props<SearchRegulatorNoticesActionPayload>()
  ),
  SELECT_LAST_RESULTS_PAGE: createAction(
    '[Regulator Notices List] Select the last results page',
    props<SearchRegulatorNoticesActionPayload>()
  ),
  SELECT_FIRST_RESULTS_PAGE: createAction(
    '[Regulator Notices List] Select the first results page',
    props<SearchRegulatorNoticesActionPayload>()
  ),
  CHANGE_PAGE_SIZE: createAction(
    '[Regulator Notices List] Change results page size',
    props<SearchRegulatorNoticesActionPayload>()
  ),
  SORT_RESULTS: createAction(
    '[Regulator Notices List] Sort results by column',
    props<SearchRegulatorNoticesActionPayload>()
  ),
  SEARCH_RESULTS_PAGE_LOADED: createAction(
    '[Regulator Notices List] Regulator Notice results and pagination info loaded',
    props<{
      results: RegulatorNoticeTask[];
      pagination: Pagination;
      sortParameters: SortParameters;
      criteria: RegulatorNoticeSearchCriteria;
    }>()
  ),
  TOGGLE_FILTERS: createAction('[Regulator Notices List] Toggle filters'),
  CLEAR_SEARCH_STATE: createAction(
    '[Regulator Notices List] Clear the state of Regulator Notice List (default mode)'
  ),
  CLEAR_SELECTION: createAction(
    '[Regulator Notices List] Clear the selected notices from store (LOAD mode)'
  ),
  RESET_RESULTS_LOADED: createAction(
    '[Regulator Notices List] Set the resultsLoaded flag to false (LOAD mode)'
  ),
  UPDATE_SELECTED: createAction(
    '[Regulator Notices List] Update the selected/checked notices',
    props<{
      added: RegulatorNoticeTask[];
      removed: RegulatorNoticeTask[];
    }>()
  ),
};
