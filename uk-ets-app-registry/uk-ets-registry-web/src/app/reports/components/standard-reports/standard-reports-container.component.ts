import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { clearErrors, errors } from '@shared/shared.action';
import {
  ReportCreationRequest,
  StandardReport,
} from '@reports/model';
import { createReportRequest, loadReportTypes } from '@reports/actions';
import { Observable } from 'rxjs';
import {
  selectIsReportSuccess,
  selectStandardReportsForRole,
} from '@reports/selectors';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-standard-reports-list-container',
  template: `
    <app-standard-reports
      [reports]="reports$ | async"
      [isRequestSuccess]="isReportSuccess$ | async"
      (generateReport)="onReportGenerated($event)"
      (errorDetails)="onError($event)"
    ></app-standard-reports>
  `,
})
export class StandardReportsContainerComponent implements OnInit {
  isReportSuccess$: Observable<boolean>;
  reports$: Observable<StandardReport[]>;
  constructor(private store: Store) {
    this.isReportSuccess$ = this.store.select(selectIsReportSuccess);
  }
  ngOnInit() {
    this.store.dispatch(loadReportTypes());
    this.reports$ = this.store.select(selectStandardReportsForRole);
  }

  onReportGenerated(payload: ReportCreationRequest) {
    this.store.dispatch(clearErrors());
    this.store.dispatch(createReportRequest({ request: payload }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
