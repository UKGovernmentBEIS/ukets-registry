import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Store } from '@ngrx/store';
import { ReportCreationRequest, StandardReport } from '@reports/model';
import { updateSelectedReport } from '@reports/actions';

@Component({
  selector: 'app-standard-reports',
  templateUrl: './standard-reports.component.html',
})
export class StandardReportsComponent {
  @Input() reports: StandardReport[];
  @Input() isRequestSuccess: boolean;
  @Output() generateReport = new EventEmitter<ReportCreationRequest>();
  selectedReport: StandardReport;

  constructor(private store: Store) {}

  generate(reportCreationPayload: ReportCreationRequest) {
    this.generateReport.emit(reportCreationPayload);
  }

  onSelectFromSearch($event: StandardReport) {
    this.selectedReport = $event;
    this.store.dispatch(updateSelectedReport({ selectedReport: $event }));
  }

  resultFormatter(item: StandardReport): string {
    return item.label;
  }
}
