import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-download-reports-list',
  template: `
    <ng-container *ngFor="let report of reports">
      <app-download-reports-list-item
        [report]="report"
        (downloadReport)="onReportDownloaded($event)"
      ></app-download-reports-list-item>
    </ng-container>
  `,
})
export class DownloadReportsListComponent implements OnInit {
  @Input() reports;
  @Output() downloadReport = new EventEmitter<number>();
  constructor() {}

  ngOnInit(): void {}

  onReportDownloaded(reportId: number) {
    this.downloadReport.emit(reportId);
  }
}
