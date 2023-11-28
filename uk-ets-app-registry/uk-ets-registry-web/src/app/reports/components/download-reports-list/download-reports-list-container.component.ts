import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Report } from '@reports/model';
import { selectReportsWithFullUserNames } from '@reports/selectors';
import {
  deactivateLoadReportsTimeoutTimer,
  downloadReport,
  loadReports,
  triggerLoadReportsTimeoutTimer,
} from '@reports/actions';

@Component({
  selector: 'app-download-reports-list-container',
  templateUrl: './download-reports-list-container.component.html',
})
export class DownloadReportsListContainerComponent
  implements OnInit, OnDestroy
{
  reports$: Observable<Report[]>;
  constructor(private store: Store) {
    this.reports$ = this.store.select(selectReportsWithFullUserNames);
  }
  ngOnDestroy(): void {
    this.store.dispatch(deactivateLoadReportsTimeoutTimer());
  }

  ngOnInit(): void {
    this.store.dispatch(loadReports());
    this.store.dispatch(triggerLoadReportsTimeoutTimer());
  }

  onReportDownloaded(reportId: number) {
    this.store.dispatch(downloadReport({ reportId }));
    this.store.dispatch(triggerLoadReportsTimeoutTimer());
  }
}
