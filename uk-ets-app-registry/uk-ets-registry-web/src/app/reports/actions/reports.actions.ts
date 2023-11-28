import { createAction, props } from '@ngrx/store';
import {
  Report,
  ReportCreationRequest,
  ReportCreationResponse,
  ReportType,
  StandardReport,
} from '@reports/model';
import { User } from '@shared/user';

export const loadReports = createAction('[Reports] Load Reports');

export const loadReportsSuccess = createAction(
  '[Reports] Load Reports Success',
  props<{ reports: Report[] }>()
);

export const loadReportUsersSuccess = createAction(
  '[Reports] Load Report Users Success',
  props<{ users: User[] }>()
);

export const triggerLoadReportsTimeoutTimer = createAction(
  '[Reports] Load Reports Timeout Timer'
);

export const deactivateLoadReportsTimeoutTimer = createAction(
  '[Reports] Deactivate Reports Timeout Timer'
);

export const createReportRequest = createAction(
  '[Reports] Create Report',
  props<{ request: ReportCreationRequest }>()
);

export const updateSelectedReport = createAction(
  '[Report] Update selected report',
  props<{ selectedReport: StandardReport }>()
);

export const createReportRequestSuccess = createAction(
  '[Reports] Create Report Success',
  props<{ response: ReportCreationResponse }>()
);

export const downloadReport = createAction(
  '[Reports] Download Report',
  props<{ reportId: number }>()
);

export const downloadReportSuccess = createAction(
  '[Reports] Download Report Success',
  props<{ report: Blob }>()
);

export const clearReportState = createAction('[Reports] Clear Report State');

export const loadReportTypes = createAction('[Reports] Load Report Types');

export const loadReportTypesSuccess = createAction(
  '[Reports] Load Report Types Success',
  props<{ reportTypes: ReportType[] }>()
);
