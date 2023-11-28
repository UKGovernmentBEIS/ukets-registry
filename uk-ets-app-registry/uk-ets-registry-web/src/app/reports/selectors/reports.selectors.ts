import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ReportRequestingRole, ReportType, reportTypeMap } from '../model';
import * as fromReports from '../reducers/reports.reducer';
import * as fromAuth from './../../auth/auth.selector';

export const selectReportsState = createFeatureSelector<fromReports.State>(
  fromReports.reportsFeatureKey
);

export const selectReports = createSelector(
  selectReportsState,
  (state) => state.reports
);

export const selectReportUsers = createSelector(
  selectReportsState,
  (state) => state.users
);

export const selectReportType = createSelector(
  selectReportsState,
  (state) => state.selectedReport
);

export const selectReportRequestingUsers = createSelector(
  selectReportsState,
  (state) => state.reports.map((report) => report.requestingUser)
);

export const selectReportsWithFullUserNames = createSelector(
  selectReports,
  selectReportUsers,
  (reports, users) =>
    reports.map((report) => {
      const user = users.find((u) => u.urid === report.requestingUser);
      if (!!user) {
        const fullUserName = `${user.firstName} ${user.lastName}`;
        return { ...report, fullUserName };
      }
      return report;
    })
);

export const selectIsReportSuccess = createSelector(
  selectReportsState,
  (state) => state?.isReportSuccess
);

export const selectReportRole = createSelector(
  fromAuth.isAdmin,
  fromAuth.isAuthorityUser,
  (isAdmin, isAuthorityUser) => {
    return !isAdmin
      ? isAuthorityUser
        ? ReportRequestingRole.AUTHORITY
        : null
      : ReportRequestingRole.ADMINISTRATOR;
  }
);

export const selectStandardReportsForRole = createSelector(
  selectReportsState,
  (state) => {
    return Object.entries(reportTypeMap)
      .filter(([type]) => state.standardReportTypes.includes(ReportType[type]))
      .sort(
        ([, typeValue1], [, typeValue2]) => typeValue1.order - typeValue2.order
      )
      .map(([type, typeValue]) => ({
        type: ReportType[type],
        label: typeValue.label,
      }));
  }
);
