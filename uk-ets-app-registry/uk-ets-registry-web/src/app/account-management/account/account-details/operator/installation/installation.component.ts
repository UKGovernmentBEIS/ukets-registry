import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Installation } from '@shared/model/account/operator';
import { Observable } from 'rxjs';
import { ImportsNotUsedAsValues } from 'typescript';

@Component({
  selector: 'app-installation',
  template: `
    <app-shared-installation
      [installation]="installation"
      [canRequestUpdate]="canRequestUpdate"
      [hasOperatorUpdatePendingApproval]="hasOperatorUpdatePendingApproval"
      [canRequestAccountTransfer]="canRequestAccountTransfer"
      (requestInstallationUpdateEmitter)="goToRequestUpdate()"
      (requestTransferAccountEmitter)="goToRequestTransferAccount()"
    ></app-shared-installation>
  `,
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

  goToRequestUpdate() {
    this.requestInstallationUpdateEmitter.emit();
  }

  goToRequestTransferAccount(): void {
    this.requestTransferAccountEmitter.emit();
  }
}
