import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  Installation,
  InstallationActivityType,
} from '../../../../model/account/operator';
import { regulatorMap } from '@account-management/account-list/account-list.model';

@Component({
  selector: 'app-shared-installation',
  templateUrl: './installation.component.html',
})
export class InstallationComponent {
  @Input()
  installation: Installation;
  @Input()
  canRequestUpdate: boolean;
  @Input()
  hasOperatorUpdatePendingApproval: boolean;
  @Input()
  canRequestAccountTransfer: boolean;
  @Input() 
  isSeniorOrJuniorAdmin: boolean;

  @Output()
  readonly requestInstallationUpdateEmitter = new EventEmitter();
  @Output()
  readonly requestTransferAccountEmitter = new EventEmitter();

  activityTypes = InstallationActivityType;
  regulators = regulatorMap;

  goToRequestUpdate() {
    this.requestInstallationUpdateEmitter.emit();
  }

  goToRequestTransferAccount() {
    this.requestTransferAccountEmitter.emit();
  }

  get summaryListLines(){
    const lines = {
      'Installation name': this.installation.name,
      'Installation ID': this.installation.identifier,
      'Installation activity type': InstallationActivityType[this.installation.activityType],
      'Emitter ID': this.installation.emitterId ? this.installation.emitterId : '',
      'Permit ID': this.installation.permit ? this.installation.permit.id : '',
      Regulator: regulatorMap[this.installation.regulator],
      'First year of verified emission submission': this.installation.firstYear,
      'Last year of verified emission submission': this.installation.lastYear
    }

    if(!this.isSeniorOrJuniorAdmin){
      const { ['Emitter ID']: emitterId, ...updatedLines } = lines;
      return updatedLines;
    }

    return lines;
  }  
}
