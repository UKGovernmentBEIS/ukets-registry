import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-search-report-button',
  templateUrl: './search-report-button.component.html',
})
export class SearchReportButtonComponent {
  @Input() criteria: any;
  @Output() readonly requestReport = new EventEmitter<any>();

  generateReport() {
    this.requestReport.emit(this.criteria);
  }
}
