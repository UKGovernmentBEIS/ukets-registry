import { createAction, props } from '@ngrx/store';
import { SearchActionPayload } from '@kp-administration/itl-messages/model';
import { Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Notice } from '@kp-administration/itl-notices/model';

export const getNotice = createAction(
  '[ITL Notices] Get notice by identifier',
  props<{ noticeIdentifier: number }>()
);

export const getNoticeSuccess = createAction(
  '[ITL Notices] Get notice by identifier success',
  props<{ notice: Notice[] }>()
);

export const loadNotices = createAction(
  '[ITL Notices] Load notices',
  props<SearchActionPayload>()
);

export const noticesLoaded = createAction(
  '[ITL Notices] Notices loaded',
  props<{
    notices: Notice[];
    pagination: Pagination;
    sortParameters: SortParameters;
  }>()
);

export const changeNoticesListPage = createAction(
  '[ITL Notices] Change page',
  props<SearchActionPayload>()
);

export const sortNotices = createAction(
  '[ITL Notices] Sort notices',
  props<SearchActionPayload>()
);

export const resetState = createAction(
  '[ITL Notices] Clear the state of ITL Notices'
);
