import { createReducer } from '@ngrx/store';

import { mutableOn } from '@shared/mutable-on';
import { Report, ReportType, StandardReport } from '@reports/model';
import {
  clearReportState,
  createReportRequest,
  createReportRequestSuccess,
  loadReportsSuccess,
  loadReportTypesSuccess,
  loadReportUsersSuccess,
  updateSelectedReport,
} from '@reports/actions';
import { User } from '@shared/user';

export const reportsFeatureKey = 'reports';

export interface State {
  users: User[];
  reports: Report[];
  isReportSuccess: boolean;
  standardReportTypes: ReportType[];
  selectedReport: StandardReport;
}

export const initialState: State = {
  users: [],
  reports: [],
  isReportSuccess: false,
  standardReportTypes: [],
  selectedReport: null,
};

export const reducer = createReducer(
  initialState,
  mutableOn(createReportRequest, (state) => {
    state.isReportSuccess = false;
  }),
  mutableOn(createReportRequestSuccess, (state) => {
    state.isReportSuccess = true;
  }),
  mutableOn(loadReportsSuccess, (state, { reports }) => {
    state.reports = reports;
  }),
  mutableOn(updateSelectedReport, (state, { selectedReport }) => {
    state.selectedReport = selectedReport;
  }),
  mutableOn(loadReportUsersSuccess, (state, { users }) => {
    state.users = users;
  }),
  mutableOn(clearReportState, (state) => {
    state.isReportSuccess = false;
  }),
  mutableOn(loadReportTypesSuccess, (state, { reportTypes }) => {
    state.standardReportTypes = reportTypes;
  })
);
