import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  accountAccessStateMap,
  AuthorisedRepresentative,
} from '@shared/model/account/authorised-representative';
import { AuthRepTableColumns, CustomColumn } from './auth-rep-table.model';
import { userStatusMap } from '@shared/user';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { AGENT_TYPE_LABELS } from '@user-management/user-details/model';
import { CRC_LABELS } from '@user-management/user-details/model/crc.model';

@Component({
  selector: 'app-shared-authorised-representative-table',
  templateUrl: './auth-rep-table.component.html',
  styleUrls: ['./auth-rep-table.component.scss'],
})
export class AuthRepTableComponent {
  @Input()
  authorisedReps: AuthorisedRepresentative[];
  @Input()
  taskNotYetApproved: boolean;
  @Input()
  customColumns: CustomColumn[];
  @Input()
  accountId: string;
  @Input()
  displayNameLink = true;
  @Input()
  displayUridLink = true;
  @Input()
  displayedColumns: AuthRepTableColumns[];
  @Input()
  highlightUsers = false;
  @Input()
  currentUserClaimant: boolean;
  @Input()
  showRequestDocumentButton: boolean;
  @Input()
  updateType: AuthorisedRepresentativesUpdateType;
  @Output() readonly requestDocumentEmitter = new EventEmitter();

  updateTypes = AuthorisedRepresentativesUpdateType;
  authRepTableColumns = AuthRepTableColumns;
  accountAccessStateMap = accountAccessStateMap;
  userStatusMap = userStatusMap;
  AGENT_TYPE_LABELS = AGENT_TYPE_LABELS;
  CRC_LABELS = CRC_LABELS;

  onUserRequestDocuments(recipientName: string, recipientUrid: string) {
    if (this.updateType && this.showRequestDocumentButton) {
      this.requestDocumentEmitter.emit({
        origin:
          this.updateType === this.updateTypes.ADD
            ? RequestDocumentsOrigin.AUTHORIZED_REPRESENTATIVE_ADDITION_TASK
            : RequestDocumentsOrigin.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
        documentsRequestType: DocumentsRequestType.USER,
        recipientName,
        recipientUrid,
      });
    }
  }
}
