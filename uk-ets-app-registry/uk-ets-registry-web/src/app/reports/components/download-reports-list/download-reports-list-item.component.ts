import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Report, ReportStatus, reportTypeMap } from '@reports/model';

@Component({
  selector: 'app-download-reports-list-item',
  templateUrl: './download-reports-list-item.component.html',
  styleUrls: ['./download-reports-list-item.component.scss'],
})
export class DownloadReportsListItemComponent implements OnInit {
  readonly Status = ReportStatus;
  readonly ReportTypeMap = reportTypeMap;

  @Input() report: Report;
  @Output() downloadReport = new EventEmitter<number>();

  constructor() {}

  ngOnInit(): void {}

  download(id: number) {
    this.downloadReport.emit(id);
  }
}
