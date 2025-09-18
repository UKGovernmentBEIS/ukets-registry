import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MaritimeOperator } from '@shared/model/account';

@Component({
  selector: 'app-maritime-operator',
  template: `
    <app-shared-maritime-operator
      [maritime]="maritime"
      [canRequestUpdate]="canRequestUpdate"
      [hasOperatorUpdatePendingApproval]="hasOperatorUpdatePendingApproval"
      [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin"           
      (requestMaritimeUpdateEmitter)="goToRequestUpdate()"
    ></app-shared-maritime-operator>
  `,
})
export class MaritimeOperatorComponent {
  @Input()
  maritime: MaritimeOperator;
  @Input()
  canRequestUpdate: boolean;
  @Input()
  hasOperatorUpdatePendingApproval: boolean;
  @Input()
  isSeniorOrJuniorAdmin: boolean;
  
  @Output()
  readonly requestMaritimeUpdateEmitter = new EventEmitter();

  goToRequestUpdate() {
    this.requestMaritimeUpdateEmitter.emit();
  }
}
