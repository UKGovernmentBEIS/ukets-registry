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
}
