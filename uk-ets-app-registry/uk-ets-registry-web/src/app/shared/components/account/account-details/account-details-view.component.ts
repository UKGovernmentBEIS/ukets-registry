import { Component, Input, OnInit } from '@angular/core';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { AccountDetails } from '@shared/model/account';
import {
  AccountType,
  AccountTypeMap,
} from '@shared/model/account/account-type.enum';

@Component({
  selector: 'app-account-details-view',
  templateUrl: './account-details-view.component.html',
  styleUrls: ['./account-details-view.component.scss'],
})
export class AccountDetailsViewComponent implements OnInit {
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

  showSalesContactInfo: boolean;
  showSalesContactDetails: boolean;

  ngOnInit() {
    const isSalesContactAccountType =
      this.accountType === AccountType.OPERATOR_HOLDING_ACCOUNT ||
      this.accountType === AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ||
      this.accountType === AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT ||
      this.accountType === AccountType.TRADING_ACCOUNT;
    const isViewMode = !this.isConfirmMode;
    const isSalesContactEmpty =
      !this.accountDetails.salesContactDetails?.emailAddress?.emailAddress &&
      !this.accountDetails.salesContactDetails?.phoneNumber;

    this.showSalesContactInfo =
      isSalesContactAccountType && isViewMode && isSalesContactEmpty;

    this.showSalesContactDetails = isSalesContactAccountType && isViewMode;
  }

  getCountryNameFromCountryCode(key: string) {
    return this.countries.find((country) => country.key === key).item[0].name;
  }
}
