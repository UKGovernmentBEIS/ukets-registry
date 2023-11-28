import { Component, Input } from '@angular/core';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { AccountDetails } from '@shared/model/account';
import {
  AccountType,
  AccountTypeMap,
} from '@shared/model/account/account-type.enum';

@Component({
  selector: 'app-billing-details-view',
  templateUrl: './billing-details-view.component.html',
})
export class BillingDetailsViewComponent {
  @Input()
  accountType: AccountType;
  @Input()
  accountDetails: AccountDetails;
  @Input()
  countries: IUkOfficialCountry[];
  @Input()
  isAdmin: boolean;
  @Input()
  isConfirmMode: boolean;

  accountTypes = AccountType;
  AccountTypeMap = AccountTypeMap;

  getCountryNameFromCountryCode(key: string) {
    return this.countries.find((country) => country.key === key).item[0].name;
  }
}
