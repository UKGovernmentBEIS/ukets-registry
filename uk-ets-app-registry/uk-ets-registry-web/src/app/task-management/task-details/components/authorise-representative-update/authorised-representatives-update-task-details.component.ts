import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  AuthoriseRepresentativeTaskDetails,
  TaskOutcome,
} from '@task-management/model';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { MenuItemEnum } from '@registry-web/account-management/account/account-details/model';

@Component({
  selector: 'app-authorise-representatives-update-task-details',
  templateUrl:
    './authorised-representatives-update-task-details.component.html',
})
export class AuthorisedRepresentativesUpdateTaskDetailsComponent {
  MenuItemEnum = MenuItemEnum;

  @Input()
  authoriseRepresentativeTaskDetails: AuthoriseRepresentativeTaskDetails;

  @Output() readonly requestDocumentEmitter = new EventEmitter();
  @Output() readonly requestPaymentEmitter = new EventEmitter();

  updateTypes = AuthorisedRepresentativesUpdateType;
  taskNotYetApproved = TaskOutcome.SUBMITTED_NOT_YET_APPROVED;

  onUserRequestDocuments(requestDocumentDetails) {
    this.requestDocumentEmitter.emit(requestDocumentDetails);
  }

  onRequestPayment(requestPaymentWizardDetails) {
    const candidateRecipients = [];
    if (this.authoriseRepresentativeTaskDetails.newUser) {
      candidateRecipients.push({
        ...this.authoriseRepresentativeTaskDetails.newUser?.user,
        urid: this.authoriseRepresentativeTaskDetails.newUser?.urid,
      });
    } else if (this.authoriseRepresentativeTaskDetails.currentUser) {
      candidateRecipients.push({
        ...this.authoriseRepresentativeTaskDetails.currentUser?.user,
        urid: this.authoriseRepresentativeTaskDetails.currentUser?.urid,
      });
    }

    this.requestPaymentEmitter.emit({
      ...requestPaymentWizardDetails,
      candidateRecipients,
    });
  }

  showRequestPaymentButton() {
    return (
      this.authoriseRepresentativeTaskDetails.arUpdateType ===
        AuthorisedRepresentativesUpdateType.ADD ||
      this.authoriseRepresentativeTaskDetails.arUpdateType ===
        AuthorisedRepresentativesUpdateType.REPLACE ||
      (this.authoriseRepresentativeTaskDetails.arUpdateType ===
        AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS &&
        this.authoriseRepresentativeTaskDetails.currentUserClaimant)
    );
  }
}
