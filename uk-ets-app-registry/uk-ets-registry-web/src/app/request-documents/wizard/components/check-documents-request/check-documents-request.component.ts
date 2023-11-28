import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RequestDocumentsRoutePaths } from '@request-documents/wizard/model/request-documents-route-paths.model';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
@Component({
  selector: 'app-check-documents-request',
  templateUrl: './check-documents-request.component.html',
})
export class CheckDocumentsRequestComponent {
  @Input()
  accountHolderName: string;
  @Input()
  accountNumber: string;
  @Input()
  accountName: string;
  @Input()
  recipientName: string;
  @Input()
  recipientUrid: string;
  @Input()
  documentNames: string[];
  @Input()
  comment: string;
  @Input()
  requestDocumentsOrigin: RequestDocumentsOrigin;
  @Input()
  displayUserCommentsPage: boolean;
  @Input()
  documentRequestType: DocumentsRequestType;
  @Input()
  isAdmin: boolean;
  @Input()
  isSeniorAdmin: boolean;

  @Output()
  readonly navigateToEmitter = new EventEmitter<RequestDocumentsRoutePaths>();
  @Output() readonly submitRequest = new EventEmitter();

  requestDocumentType = DocumentsRequestType;
  requestDocumentsRoutePaths = RequestDocumentsRoutePaths;

  getTitleText(): string {
    return this.accountHolderName
      ? 'Request account holder documents'
      : 'Request user documents';
  }

  onContinue() {
    this.submitRequest.emit();
  }

  navigateTo(path) {
    this.navigateToEmitter.emit(path);
  }

  getRecipientHeaderSummaryListItems(): SummaryListItem[] {
    return [
      {
        key: {
          label: 'Recipient details',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
            class: '',
          },
        ],
      },
    ];
  }

  getRecipientSummaryListItems(): SummaryListItem[] {
    const results = [];
    results.push({
      key: { label: 'Name' },
      value: [
        {
          label: this.recipientName,
          class:
            'govuk-summary-list__value govuk-summary-list__value_change_description',
        },
        {
          label: '',
          class: '',
        },
      ],
    });
    if (this.isAdmin || this.isSeniorAdmin) {
      results.push({
        key: { label: 'User Id' },
        value: [
          {
            label: this.recipientUrid,
            class:
              'govuk-summary-list__value govuk-summary-list__value_change_description recipient_urid_link',
          },
          {
            label: '',
            class: '',
          },
        ],
      });
    }
    return results;
  }

  getAccountHeaderSummaryListItems(): SummaryListItem[] {
    return [
      {
        key: {
          label: 'Account details',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
            class: '',
          },
        ],
      },
    ];
  }

  getAccountSummaryListItems(): SummaryListItem[] {
    const results = [];
    if (this.accountHolderName) {
      results.push({
        key: { label: 'Account Holder' },
        value: [
          {
            label: this.accountHolderName,
            class:
              'govuk-summary-list__value govuk-summary-list__value_change_description',
          },
          {
            label: '',
            class: '',
          },
        ],
      });
    }
    if (this.accountNumber) {
      results.push({
        key: { label: 'Account number' },
        value: [
          {
            label: this.accountNumber,
            class:
              'govuk-summary-list__value govuk-summary-list__value_change_description',
          },
          {
            label: '',
            class: '',
          },
        ],
      });
    }
    if (this.accountName) {
      results.push({
        key: { label: 'Account name' },
        value: [
          {
            label: this.accountName,
            class:
              'govuk-summary-list__value govuk-summary-list__value_change_description',
          },
          {
            label: '',
            class: '',
          },
        ],
      });
    }
    return results;
  }

  getCommentsHeaderSummaryListItems(): SummaryListItem[] {
    const results = [];
    if (this.displayUserCommentsPage) {
      results.push({
        key: {
          label: 'Why you are assigning this task',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
            class: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          url: '',
          clickEvent: this.requestDocumentsRoutePaths['assigning-user-comment'],
        },
      });
    } else {
      results.push({
        key: {
          label: 'Why you are assigning this task',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
            class: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          url: '',
          clickEvent: this.requestDocumentsRoutePaths['select-recipient'],
        },
      });
    }
    return results;
  }

  getCommentsSummaryListItems(): SummaryListItem[] {
    return [
      {
        key: {
          label: '',
          class: 'forcibly-hide',
        },
        value: [
          {
            label: this.comment,
            class: '',
          },
        ],
      },
    ];
  }
}
