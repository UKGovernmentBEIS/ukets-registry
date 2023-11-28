import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { selectCountryCodes } from '@shared/shared.selector';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { AccountDetails } from '@shared/model/account/account-details';
import { Account, AccountType } from '@shared/model/account';
import { CountryCodeModel } from '@shared/countries/country-code.model';
import { isAdmin, isSeniorAdmin } from '@registry-web/auth/auth.selector';
import {
  selectAccount,
  selectAccountDetailsForEdit,
  selectAccountDetailsSameBillingAddress,
} from '@account-management/account/account-details/account.selector';
import { selectCountries } from '@registry-web/registration/check-answers-and-submit/check-answers-and-submit.selectors';
import {
  accountDetailsSameBillingAddress,
  cancelUpdateAccountDetails,
  updateAccountDetails,
} from '../account.actions';

@Component({
  selector: 'app-edit-billing-details-container',
  template: `
    <app-billing-details-form
      [updateMode]="true"
      [accountType]="(account$ | async).accountType"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      [isAdmin]="isAdmin$ | async"
      [isSeniorAdmin]="isSeniorAdmin$ | async"
      [accountHolder]="(account$ | async).accountHolder"
      [accountHolderType]="(account$ | async).accountHolder.type"
      [accountDetails]="accountDetails$ | async"
      [accountDetailsSameBillingAddress]="
        accountDetailsSameBillingAddress$ | async
      "
      (accountDetailsOutput)="onContinue($event)"
      (errorDetails)="onError($event)"
      (copyAccountHolderAddressAccountDetails)="
        onSameAddressCheckChange($event)
      "
    >
    </app-billing-details-form>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditBillingDetailsContainerComponent implements OnInit {
  account$: Observable<Account>;
  accountDetails$: Observable<AccountDetails>;
  accountType$: Observable<AccountType>;
  countries$: Observable<IUkOfficialCountry[]>;
  countryCodes$: Observable<CountryCodeModel[]>;
  isAdmin$: Observable<boolean>;
  isSeniorAdmin$: Observable<boolean>;
  accountDetailsSameBillingAddress$: Observable<boolean>;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.account$ = this.store.select(selectAccount);
    this.accountDetails$ = this.store.select(selectAccountDetailsForEdit);
    this.countries$ = this.store.select(selectCountries);
    this.countryCodes$ = this.store.select(selectCountryCodes);
    this.isAdmin$ = this.store.select(isAdmin);
    this.isSeniorAdmin$ = this.store.select(isSeniorAdmin);
    this.accountDetailsSameBillingAddress$ = this.store.select(
      selectAccountDetailsSameBillingAddress
    );

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get(
          'accountId'
        )}/edit`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onContinue(accountDetails: AccountDetails) {
    this.store.dispatch(updateAccountDetails({ accountDetails }));

    this._router.navigate(
      [
        `/account/${this.activatedRoute.snapshot.paramMap.get(
          'accountId'
        )}/edit-confirm`,
      ],
      {
        skipLocationChange: true,
      }
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelUpdateAccountDetails({
        currentRoute: this.activatedRoute.snapshot['_routerState'].url,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onSameAddressCheckChange(event) {
    this.store.dispatch(
      accountDetailsSameBillingAddress({
        accountDetailsSameBillingAddress: event,
      })
    );
  }
}
