import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AuthorisedRepresentative } from '@shared/model/account';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { AuthRepTableColumns } from '../../authorised-representative-table';

@Component({
  selector: 'app-ar-update-user',
  templateUrl: './ar-update-user.component.html',
})
export class ArUpdateUserComponent {
  @Input()
  isTaskDetailsPage: boolean;
  @Input()
  taskNotYetApproved: boolean;
  @Input()
  currentUserClaimant: boolean;
  @Input()
  updateType: AuthorisedRepresentativesUpdateType;
  @Input()
  currentUser: AuthorisedRepresentative;
  @Input()
  newUser: AuthorisedRepresentative;
  @Input()
  displayedChangeLink: boolean;
  @Input()
  addAndRemoveTextDisplayed = true;
  @Output() readonly clickChange = new EventEmitter();
  @Output() readonly requestDocumentEmitter = new EventEmitter();

  displayedColumnsForAr: AuthRepTableColumns[] = [
    AuthRepTableColumns.NAME,
    AuthRepTableColumns.ACCESS_RIGHTS,
    AuthRepTableColumns.USER_ID,
    AuthRepTableColumns.WORK_CONTACT,
    AuthRepTableColumns.AR_STATUS,
    AuthRepTableColumns.USER_STATUS,
  ];

  updateTypes = AuthorisedRepresentativesUpdateType;

  onChangeClicked() {
    this.clickChange.emit();
  }

  computeActionMessage() {
    switch (this.updateType) {
      case AuthorisedRepresentativesUpdateType.ADD:
        return this.addAndRemoveTextDisplayed
          ? 'You are adding the following authorised representative'
          : '';
      case AuthorisedRepresentativesUpdateType.REPLACE:
        return 'Replace the following authorised representative';
      case AuthorisedRepresentativesUpdateType.REMOVE:
        return this.addAndRemoveTextDisplayed
          ? 'You will remove the following authorised representative from the account'
          : '';
    }
    return '';
  }

  onUserRequestDocuments(requestDocumentDetails) {
    this.requestDocumentEmitter.emit(requestDocumentDetails);
  }
}
