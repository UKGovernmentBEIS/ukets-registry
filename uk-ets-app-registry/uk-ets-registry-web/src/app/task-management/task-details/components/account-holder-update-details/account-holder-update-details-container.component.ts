import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { AccountHolderUpdateDetails } from '@task-management/model';
import { getLabel } from '@shared/shared.util';
import { ACCOUNT_TYPE_OPTIONS } from '@task-management/task-list/task-search/search-tasks-form/search-tasks-form.model';
import { AccountHolder } from '@shared/model/account';
import { AccountHolderInfoChanged } from '@account-management/account/account-holder-details-wizard/model';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import { IUkOfficialCountry } from '@registry-web/shared/countries/country.interface';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { selectAllCountries } from '@registry-web/shared/shared.selector';

@Component({
  selector: 'app-account-holder-update-details-container',
  templateUrl: './account-holder-update-details-container.component.html',
})
export class AccountHolderUpdateDetailsContainerComponent implements OnInit {
  @Input()
  taskDetails: AccountHolderUpdateDetails;

  @Output() readonly requestDocumentEmitter = new EventEmitter();

  constructor(private store: Store) {}

  isCompleted: boolean;

  currentValues: {
    accountHolder: AccountHolder;
  };
  changedValues: {
    accountHolderDiff: AccountHolderInfoChanged;
  };

  countries$: Observable<IUkOfficialCountry[]>;
  getLabel(value: string): string {
    return getLabel(value, ACCOUNT_TYPE_OPTIONS);
  }

  ngOnInit(): void {
    this.currentValues = {
      accountHolder: this.taskDetails.accountHolder,
    };
    this.changedValues = {
      accountHolderDiff: this.taskDetails.accountHolderDiff,
    };
    this.isCompleted = this.taskDetails.taskStatus === 'COMPLETED';
    this.countries$ = this.store.select(selectAllCountries);
  }

  onAccountHolderRequestDocuments() {
    this.requestDocumentEmitter.emit({
      parentRequestId: this.taskDetails.requestId,
      origin: RequestDocumentsOrigin.ACCOUNT_HOLDER_UPDATE_DETAILS_TASK,
      documentsRequestType: DocumentsRequestType.ACCOUNT_HOLDER,
      accountHolderIdentifier: this.taskDetails.accountHolder.id,
      accountName: this.taskDetails.accountDetails.name,
      accountFullIdentifier: this.taskDetails.accountFullIdentifier,
      accountHolderName: this.taskDetails.accountHolder.details.name,
      recipientName: this.taskDetails.initiatorName,
      recipientUrid: this.taskDetails.initiatorUrid,
    });
  }
}
