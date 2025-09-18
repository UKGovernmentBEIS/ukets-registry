import { Component, Input, EventEmitter, Output } from '@angular/core';
import { regulatorMap } from '@account-management/account-list/account-list.model';
import { MaritimeOperator } from '@registry-web/shared/model/account';

@Component({
  selector: 'app-shared-maritime-operator',
  templateUrl: './maritime-operator.component.html',
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

  regulators = regulatorMap;

  goToRequestUpdate() {
    this.requestMaritimeUpdateEmitter.emit();
  }

  get summaryListLines(){
    const lines = {
      'Maritime Operator ID': this.maritime.identifier,
      'Emitter ID': this.maritime.emitterId ? this.maritime.emitterId : '',
      'Monitoring plan ID': this.maritime.monitoringPlan
        ? this.maritime.monitoringPlan.id
        : '',
      'Company IMO number': this.maritime.imo,
      Regulator: regulatorMap[this.maritime.regulator],
      'First year of verified emission submission': this.maritime.firstYear,
      'Last year of verified emission submission': this.maritime.lastYear,
    }

    if(!this.isSeniorOrJuniorAdmin){
      const { ['Emitter ID']: emitterId, ...updatedLines } = lines;
      return updatedLines;
    }

    return lines;
  }
}
