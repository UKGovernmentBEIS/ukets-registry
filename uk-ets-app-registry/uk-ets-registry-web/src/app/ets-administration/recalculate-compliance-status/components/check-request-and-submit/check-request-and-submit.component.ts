import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-check-request-and-submit',
  templateUrl: './check-request-and-submit.component.html',
  styles: [],
})
export class CheckRequestAndSubmitComponent {
  @Output() readonly startRecalculationComplianceStatusClick =
    new EventEmitter<void>();

  startRecalculationComplianceStatusAllEntities(): void {
    this.startRecalculationComplianceStatusClick.next();
  }
}
