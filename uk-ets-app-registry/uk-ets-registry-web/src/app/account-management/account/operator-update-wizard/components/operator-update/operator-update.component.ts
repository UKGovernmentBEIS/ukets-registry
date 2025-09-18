import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Operator, OperatorType } from '@shared/model/account';
import { ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-operator-update',
  template: `<ng-container *ngIf="isInstallationOperator"
      ><app-installation-update
        [operatorInfo]="operatorInfo | installation"
        [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin"
        (cancelEmitter)="onCancel()"
        (errorEmitter)="onErrors($event)"
        (continueEmitter)="onContinue($event)"
      ></app-installation-update
    ></ng-container>
    <ng-container *ngIf="isAircraftOperator"
      ><app-aircraft-update
        [operatorInfo]="operatorInfo | aircraftOperator"
        [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin"
        (cancelEmitter)="onCancel()"
        (errorEmitter)="onErrors($event)"
        (continueEmitter)="onContinue($event)"
      ></app-aircraft-update
    ></ng-container>
    <ng-container *ngIf="isMaritimeOperator"
      ><app-maritime-update
        [operatorInfo]="operatorInfo | maritimeOperator"
        [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin"
        [emissionStartYear]="emissionStartYear"
        (cancelEmitter)="onCancel()"
        (errorEmitter)="onErrors($event)"
        (continueEmitter)="onContinue($event)"
      ></app-maritime-update
    ></ng-container>`,
})
export class OperatorUpdateComponent implements OnInit {
  @Input()
  operatorInfo: Operator;
  @Input()
  isSeniorOrJuniorAdmin: boolean;
  @Input()
  emissionStartYear: number;

  @Output() readonly cancelEmitter = new EventEmitter();
  @Output() readonly errorEmitter = new EventEmitter<ErrorSummary>();
  @Output() readonly continueEmitter = new EventEmitter<Operator>();

  isInstallationOperator: boolean;
  isAircraftOperator: boolean;
  isMaritimeOperator: boolean;

  ngOnInit() {
    this.isInstallationOperator =
      this.operatorInfo.type === OperatorType.INSTALLATION;
    this.isAircraftOperator =
      this.operatorInfo.type === OperatorType.AIRCRAFT_OPERATOR;
    this.isMaritimeOperator =
      this.operatorInfo.type === OperatorType.MARITIME_OPERATOR;
  }

  onContinue(value: Operator) {
    this.continueEmitter.emit(value);
  }

  onErrors(value: ErrorSummary) {
    this.errorEmitter.emit(value);
  }

  onCancel() {
    this.cancelEmitter.emit();
  }
}
