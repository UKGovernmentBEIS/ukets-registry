import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Store } from '@ngrx/store';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Account, AccountType } from '@shared/model/account';
import { saveExcludeBillingRemarks } from '../account.actions';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
})
export class DetailsComponent {
  @Input()
  account: Account;
  @Input()
  accountType: AccountType;
  @Input()
  countries: IUkOfficialCountry[];
  @Input()
  canRequestUpdate: boolean;
  @Input()
  canExcludeFromBilling: boolean;
  @Input()
  canIncludeInBilling: boolean;
  @Input()
  isAdmin: boolean;
  @Output()
  includeInBilling = new EventEmitter<boolean>();

  constructor(private store: Store) {}

  inlcudeInBillingClick() {
    this.includeInBilling.emit(true);
  }

  excludeBillingClick() {
    this.store.dispatch(saveExcludeBillingRemarks({ remarks: null }));
  }
}
