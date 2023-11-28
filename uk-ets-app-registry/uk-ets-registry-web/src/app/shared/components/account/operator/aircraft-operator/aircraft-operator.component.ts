import { Component, Input, EventEmitter, Output } from '@angular/core';
import { AircraftOperator } from '../../../../model/account/operator';
import { regulatorMap } from '@account-management/account-list/account-list.model';

@Component({
  selector: 'app-shared-aircraft-operator',
  templateUrl: './aircraft-operator.component.html',
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

  regulators = regulatorMap;

  goToRequestUpdate() {
    this.requestAircraftUpdateEmitter.emit();
  }
}
