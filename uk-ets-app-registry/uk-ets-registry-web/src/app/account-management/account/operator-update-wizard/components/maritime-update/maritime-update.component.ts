import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { MaritimeOperator } from '@shared/model/account';
import {Observable} from "rxjs";
import {selectRegistryConfigurationProperty} from "@shared/shared.selector";

@Component({
  selector: 'app-maritime-update',
  template: `<app-maritime-input
      [maritime]="operatorInfo"
      [title]="'Update the Maritime Operator details'"
      [headerTitle]="'Request to update the Maritime Operator information'"
      [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin"
      [emissionStartYear]="emissionStartYear"
      (maritimeOutput)="onContinue($event)"
      (errorDetails)="onErrors($event)"
    ></app-maritime-input
    ><app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
})
export class MaritimeUpdateComponent {
  @Input()
  operatorInfo: MaritimeOperator;
  @Input()
  isSeniorOrJuniorAdmin: boolean;
  @Input()
  emissionStartYear: number;

  @Output() readonly cancelEmitter = new EventEmitter();
  @Output() readonly errorEmitter = new EventEmitter<ErrorSummary>();
  @Output() readonly continueEmitter = new EventEmitter<MaritimeOperator>();

  onContinue(value: MaritimeOperator) {
    this.continueEmitter.emit(value);
  }

  onErrors(value: ErrorDetail[]) {
    this.errorEmitter.emit(new ErrorSummary(value));
  }

  onCancel() {
    this.cancelEmitter.emit();
  }
}
