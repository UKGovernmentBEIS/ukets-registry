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

  updateTypes = AuthorisedRepresentativesUpdateType;
  taskNotYetApproved = TaskOutcome.SUBMITTED_NOT_YET_APPROVED;

  onUserRequestDocuments(requestDocumentDetails) {
    this.requestDocumentEmitter.emit(requestDocumentDetails);
  }
}
