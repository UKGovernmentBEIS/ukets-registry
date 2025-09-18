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
  @Input() 
  isSeniorOrJuniorAdmin: boolean;     
  
  @Output()
  readonly requestAircraftUpdateEmitter = new EventEmitter();

  regulators = regulatorMap;

  goToRequestUpdate() {
    this.requestAircraftUpdateEmitter.emit();
  }

  get summaryListLines(){
    const lines = {
      'Aircraft Operator ID': this.aircraft.identifier,
      'Emitter ID': this.aircraft.emitterId ? this.aircraft.emitterId : '',
      'Monitoring plan ID': this.aircraft.monitoringPlan
        ? this.aircraft.monitoringPlan.id
        : '',
      Regulator: regulatorMap[this.aircraft.regulator],
      'First year of verified emission submission': this.aircraft.firstYear,
      'Last year of verified emission submission': this.aircraft.lastYear
    }

    if(!this.isSeniorOrJuniorAdmin){
      const { ['Emitter ID']: emitterId, ...updatedLines } = lines;
      return updatedLines;
    }

    return lines;
  }
}
