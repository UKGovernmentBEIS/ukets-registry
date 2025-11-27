import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AccountTransferTaskDetails } from '@task-management/model';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import { SummaryListItem } from '@registry-web/shared/summary-list/summary-list.info';
import { InstallationActivityType } from '@registry-web/shared/model/account';
import { regulatorMap } from '@registry-web/account-management/account-list/account-list.model';

@Component({
  selector: 'app-account-transfer-task-details',
  templateUrl: './account-transfer-task-details.component.html',
})
export class AccountTransferTaskDetailsComponent implements OnInit {
  @Input() taskDetails: AccountTransferTaskDetails;
  @Input() isSeniorOrJuniorAdmin: boolean;    
  @Output() readonly requestDocumentEmitter = new EventEmitter();

  activityTypes = InstallationActivityType;
  regulatorMap = regulatorMap;

  isCompleted: boolean;

  ngOnInit(): void {
    this.isCompleted = this.taskDetails.taskStatus === 'COMPLETED';
  }

  onAccountHolderRequestDocuments() {
    this.requestDocumentEmitter.emit({
      parentRequestId: this.taskDetails.requestId,
      origin: RequestDocumentsOrigin.ACCOUNT_TRANSFER_TASK,
      documentsRequestType: DocumentsRequestType.ACCOUNT_HOLDER,
      accountHolderIdentifier: this.taskDetails.action.accountHolderDTO.id,
      accountHolderName: this.taskDetails.action.accountHolderDTO.details.name,
      accountName: this.taskDetails.account.name,
      recipientName: this.taskDetails.initiatorName,
      recipientUrid: this.taskDetails.initiatorUrid,
    });
  }

  getInstallationSummaryListItems(): SummaryListItem[] {
    const summary = [
      {
        key: {
          label: 'Installation details',
          class: 'summary-list-change-header-font-weight govuk-body-l',
        },
        value: [
          {
            label: '',
            class: '',
          },
        ],
      },
      {
        key: { label: 'Installation ID' },
        value: {
          label: this.taskDetails.action.installationDetails?.identifier,
        },
      },
      {
        key: { label: 'Installation name' },
        value: { label: this.taskDetails.action.installationDetails?.name },
      },
      {
        key: { label: 'Installation activity type' },
        value: {
          label:
            this.activityTypes[
              this.taskDetails.action.installationDetails?.activityType
            ],
        },
      },
      {
        key: { label: 'Emitter ID' },
        value: {
          label: this.taskDetails.action.installationDetails?.emitterId,
        },
      },
      {
        key: { label: 'Permit ID' },
        value: {
          label: this.taskDetails.action.installationDetails?.permit.id,
        },
      },
      {
        key: { label: 'Regulator' },
        value: {
          label:
            regulatorMap[
              this.taskDetails.action.installationDetails?.regulator
            ],
        },
        projection: 'changeRegulatorForm',
      },
      {
        key: { label: 'First year of verified emission submission' },
        value: {
          label: this.taskDetails.action.installationDetails?.firstYear,
        },
      },
      {
        key: { label: 'Last year of verified emission submission' },
        value: { label: this.taskDetails.action.installationDetails?.lastYear },
      },
    ];


    return !this.isSeniorOrJuniorAdmin ?
      summary.filter( next => next.key.label != 'Emitter ID') :
        summary;     
  }
}
