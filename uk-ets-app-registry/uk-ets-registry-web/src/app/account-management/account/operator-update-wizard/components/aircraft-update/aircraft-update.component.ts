import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AircraftOperator } from '@shared/model/account';

@Component({
  selector: 'app-aircraft-update',
  template: `<app-aircraft-input
      [aircraft]="operatorInfo"
      [title]="'Update the Aircraft Operator details'"
      [headerTitle]="'Request to update the Aircraft Operator information'"
      [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin"
      (aircraftOutput)="onContinue($event)"
      (errorDetails)="onErrors($event)"
    ></app-aircraft-input
    ><app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
})
export class AircraftUpdateComponent {
  @Input()
  operatorInfo: AircraftOperator;
  @Input()
  isSeniorOrJuniorAdmin: boolean;

  @Output() readonly cancelEmitter = new EventEmitter();
  @Output() readonly errorEmitter = new EventEmitter<ErrorSummary>();
  @Output() readonly continueEmitter = new EventEmitter<AircraftOperator>();

  onContinue(value: AircraftOperator) {
    this.continueEmitter.emit(value);
  }

  onErrors(value: ErrorDetail[]) {
    this.errorEmitter.emit(new ErrorSummary(value));
  }

  onCancel() {
    this.cancelEmitter.emit();
  }
}
