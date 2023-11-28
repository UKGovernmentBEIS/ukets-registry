import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AircraftOperator } from '@shared/model/account';

@Component({
  selector: 'app-aircraft-operator',
  template: `
    <app-shared-aircraft-operator
      [aircraft]="aircraft"
      [canRequestUpdate]="canRequestUpdate"
      [hasOperatorUpdatePendingApproval]="hasOperatorUpdatePendingApproval"
      (requestAircraftUpdateEmitter)="goToRequestUpdate()"
    ></app-shared-aircraft-operator>
  `,
})
export class AircraftOperatorComponent {
  @Input()
  aircraft: AircraftOperator;
  @Input()
  canRequestUpdate: boolean;
  @Input()
  hasOperatorUpdatePendingApproval: boolean;

  @Output()
  readonly requestAircraftUpdateEmitter = new EventEmitter();

  goToRequestUpdate() {
    this.requestAircraftUpdateEmitter.emit();
  }
}
