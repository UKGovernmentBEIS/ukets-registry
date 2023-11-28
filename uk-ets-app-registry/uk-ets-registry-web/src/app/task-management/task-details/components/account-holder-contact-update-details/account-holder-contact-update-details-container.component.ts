import { Component, Input, OnInit } from '@angular/core';
import {
  AccountHolderPrimaryContactUpdateDetails,
  RequestType,
} from '@task-management/model';
import { getLabel } from '@shared/shared.util';
import { ACCOUNT_TYPE_OPTIONS } from '@task-management/task-list/task-search/search-tasks-form/search-tasks-form.model';
import { AccountHolderContact } from '@shared/model/account';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { Store } from '@ngrx/store';
import { selectAllCountries } from '@registry-web/shared/shared.selector';
import { Observable } from 'rxjs';
import { IUkOfficialCountry } from '@registry-web/shared/countries/country.interface';

@Component({
  selector: 'app-account-holder-contact-update-details-container',
  templateUrl:
    './account-holder-contact-update-details-container.component.html',
})
export class AccountHolderContactUpdateDetailsContainerComponent
  implements OnInit
{
  @Input()
  taskDetails: AccountHolderPrimaryContactUpdateDetails;

  constructor(private store: Store) {}

  deleteAlternativePrimaryContactType =
    RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE;
  addAlternativePrimaryContactType =
    RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD;

  currentValues: {
    accountHolderContact: AccountHolderContact;
  };
  changedValues: {
    accountHolderContactDiff: AccountHolderContact;
  };

  countries$: Observable<IUkOfficialCountry[]>;

  getLabel(value: string): string {
    return getLabel(value, ACCOUNT_TYPE_OPTIONS);
  }

  ngOnInit(): void {
    this.currentValues = {
      accountHolderContact: this.taskDetails.accountHolderContact,
    };
    if (this.taskDetails.accountHolderContactDiff) {
      this.changedValues = {
        accountHolderContactDiff: this.taskDetails.accountHolderContactDiff,
      };
    }
    this.countries$ = this.store.select(selectAllCountries);
  }

  getContactType() {
    return this.taskDetails.taskType ===
      RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS
      ? ContactType.PRIMARY
      : ContactType.ALTERNATIVE;
  }
}
