import { createAction, props } from '@ngrx/store';
import { RegulatorNoticeTaskDetails } from '@shared/task-and-regulator-notice-management/model';
import { DomainEvent } from '@registry-web/shared/model/event';
import { TaskFileDownloadInfo } from '@shared/task-and-regulator-notice-management/model';

export const RegulatorNoticeDetailsActions = {
  LOAD_DETAILS: createAction(
    '[Regulator Notice Details] Load details',
    props<{ noticeDetails: RegulatorNoticeTaskDetails }>()
  ),

  COMPLETE: createAction('[Regulator Notice Details] Complete notice'),
  COMPLETE_SUCCESS: createAction(
    '[Regulator Notice Details] Complete notice success',
    props<{ completedNoticeDetails: RegulatorNoticeTaskDetails }>()
  ),

  FETCH_HISTORY: createAction(
    '[Regulator Notice Details] Fetch history',
    props<{ requestId: string }>()
  ),
  FETCH_HISTORY_SUCCESS: createAction(
    '[Regulator Notice Details] Fetch history success',
    props<{ results: DomainEvent[] }>()
  ),
  FETCH_HISTORY_ERROR: createAction(
    '[Regulator Notice Details] Fetch history error',
    props<{ error?: any }>()
  ),
  ADD_HISTORY_COMMENT: createAction(
    '[Regulator Notice Details] Add history comment',
    props<{ requestId: string; comment: string }>()
  ),
  ADD_HISTORY_COMMENT_SUCCESS: createAction(
    '[Regulator Notice Details] Add history comment success',
    props<{ requestId: string }>()
  ),
  ADD_HISTORY_COMMENT_ERROR: createAction(
    '[Regulator Notice Details] Add history comment error',
    props<{ error?: any }>()
  ),

  RESET: createAction('[Regulator Notice Details] Reset'),

  FETCH_FILE: createAction(
    '[Regulator Notice Details] Fetch file',
    props<{ info: TaskFileDownloadInfo }>()
  ),
};
