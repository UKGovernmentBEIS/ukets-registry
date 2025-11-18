import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';

@Component({
  selector: 'app-cancel-request-documents',
  templateUrl: './cancel-request-documents.component.html',
})
export class CancelRequestDocumentsComponent {
  @Input()
  requestDocumentsOrigin: RequestDocumentsOrigin;
  @Output() readonly cancelProposal = new EventEmitter();

  getReturningPageName() {
    switch (this.requestDocumentsOrigin) {
      case RequestDocumentsOrigin.ACCOUNT_DETAILS:
        return 'account';
      case RequestDocumentsOrigin.USER:
        return 'user';
      case RequestDocumentsOrigin.ACCOUNT_OPENING_TASK:
        return 'open account task';
      case RequestDocumentsOrigin.AUTHORIZED_REPRESENTATIVE_ADDITION_TASK:
        return 'add Authorised Representative task';
      case RequestDocumentsOrigin.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST:
        return 'replace Authorised Representative task';
      case RequestDocumentsOrigin.ACCOUNT_HOLDER_UPDATE_DETAILS_TASK:
        return 'account holder update details task';
      case RequestDocumentsOrigin.CHANGE_ACCOUNT_HOLDER_TASK:
        return 'change account holder task';
    }
  }

  onCancel() {
    this.cancelProposal.emit();
  }
}
