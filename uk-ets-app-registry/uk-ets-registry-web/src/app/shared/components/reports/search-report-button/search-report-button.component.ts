import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-search-report-button',
  templateUrl: './search-report-button.component.html',
})
export class SearchReportButtonComponent implements OnInit {
  @Input() criteria: any;
  @Output() readonly requestReport = new EventEmitter<any>();

  constructor() {}

  ngOnInit(): void {}

  generateReport() {
    this.requestReport.emit(this.criteria);
  }
}
