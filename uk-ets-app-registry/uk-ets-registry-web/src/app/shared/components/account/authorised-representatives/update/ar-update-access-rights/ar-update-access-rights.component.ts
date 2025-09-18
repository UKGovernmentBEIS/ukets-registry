import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { ARAccessRights } from '@shared/model/account';

@Component({
  selector: 'app-ar-update-access-rights',
  templateUrl: './ar-update-access-rights.component.html',
  styleUrls: ['./ar-update-access-rights.component.scss'],
})
export class ArUpdateAccessRightsComponent {
  @Input()
  updateType: AuthorisedRepresentativesUpdateType;
  @Input()
  accessRights: ARAccessRights;
  @Input()
  displayedChangeLink: boolean;
  @Input()
  showRequestPaymentButton: boolean;
  @Output() readonly clickChange = new EventEmitter();
  @Output() readonly clickRequestPayment = new EventEmitter();

  updateTypes = AuthorisedRepresentativesUpdateType;

  onChangeClicked() {
    this.clickChange.emit();
  }

  computeAccessRightsTitle() {
    switch (this.updateType) {
      case AuthorisedRepresentativesUpdateType.ADD:
      case AuthorisedRepresentativesUpdateType.REPLACE:
        return 'Permissions for the new user';
      case AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS:
        return 'New permissions';
    }
  }

  onRequestPayment() {
    this.clickRequestPayment.emit({
      origin: this.computeRequestPaymentOrigin(),
    });
  }

  computeRequestPaymentOrigin() {
    switch (this.updateType) {
      case AuthorisedRepresentativesUpdateType.ADD:
        return 'ADD_AUTHORISED_REPRESENTATIVE';
      case AuthorisedRepresentativesUpdateType.REPLACE:
        return 'REPLACE_AUTHORISED_REPRESENTATIVE';
      case AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS:
        return 'CHANGE_AUTHORISED_REPRESENTATIVE_PERMISSION';
    }
  }
}
