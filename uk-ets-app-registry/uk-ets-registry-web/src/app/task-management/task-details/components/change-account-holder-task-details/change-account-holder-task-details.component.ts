import { Component, computed, input, output } from '@angular/core';
import { ChangeAccountHolderTaskMap } from '@registry-web/account-management/account/change-account-holder-wizard/change-account-holder.task-map';
import { DocumentsRequestType } from '@registry-web/shared/model/request-documents/documents-request-type';
import { RequestDocumentsOrigin } from '@registry-web/shared/model/request-documents/request-documents-origin';
import { isNil } from '@registry-web/shared/shared.util';
import { AccountHolderChangeTaskDetails } from '@registry-web/task-management/model';

@Component({
  selector: 'app-change-account-holder-task-details',
  templateUrl: './change-account-holder-task-details.component.html',
})
export class ChangeAccountHolderTaskDetailsComponent {
  readonly isNil = isNil;

  readonly taskDetails = input.required<AccountHolderChangeTaskDetails>();
  readonly requestDocumentEmitter = output<any>();

  readonly map = ChangeAccountHolderTaskMap;
  readonly showRequestDocumentsButton = computed<boolean>(
    () =>
      this.taskDetails()?.currentUserClaimant &&
      this.taskDetails()?.taskStatus !== 'COMPLETED'
  );
  readonly fromAccountHolder = computed(
    () => this.taskDetails().currentAccountHolder
  );
  readonly toAccountHolder = computed(
    () => this.taskDetails().action.accountHolderDTO
  );
  readonly toAccountHolderContact = computed(
    () => this.taskDetails().action.accountHolderContactInfo
  );
  readonly accountHolderDelete = computed(
    () => this.taskDetails().action.accountHolderDelete
  );
  readonly warningMessage = computed<string | null>(() =>
    this.accountHolderDelete()
      ? ChangeAccountHolderTaskMap.DELETE_ORPHAN_ACCOUNT_HOLDER_WARNING
      : null
  );

  onRequestDocuments() {
    this.requestDocumentEmitter.emit({
      parentRequestId: this.taskDetails().requestId,
      origin: RequestDocumentsOrigin.CHANGE_ACCOUNT_HOLDER_TASK,
      documentsRequestType: DocumentsRequestType.ACCOUNT_HOLDER,
      accountHolderIdentifier: this.toAccountHolder().id,
      accountName: this.taskDetails().accountName,
      accountFullIdentifier: this.taskDetails().accountFullIdentifier,
      accountHolderName: this.toAccountHolder().details.name,
      recipientName: this.taskDetails().initiatorName,
      recipientUrid: this.taskDetails().initiatorUrid,
    });
  }
}
