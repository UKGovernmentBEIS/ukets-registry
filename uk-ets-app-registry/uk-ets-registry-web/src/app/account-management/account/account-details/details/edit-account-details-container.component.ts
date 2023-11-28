import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, map, take } from 'rxjs';
import {
  selectAccount,
  selectAccountDetailsForEdit,
  selectAccountDetailsSameBillingAddress,
} from '@account-management/account/account-details/account.selector';
import { Account, AccountDetails, AccountType } from '@shared/model/account';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { selectCountries } from '@registry-web/registration/check-answers-and-submit/check-answers-and-submit.selectors';
import { ErrorDetail } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import {
  accountDetailsSameBillingAddress,
  cancelUpdateAccountDetails,
  updateAccountDetails,
} from '@account-management/account/account-details/account.actions';
import { CountryCodeModel } from '@shared/countries/country-code.model';
import { selectCountryCodes } from '@shared/shared.selector';
import { isAdmin, isSeniorAdmin } from '@registry-web/auth/auth.selector';
import { IsBillablePipe } from '@registry-web/shared/pipes';
import { AccountDetailsWizardRoutes } from '@registry-web/account-opening/account-details/account-details-wizard-properties';

@Component({
  selector: 'app-edit-account-details-container',
  template: `
    <app-account-details-form
      [updateMode]="true"
      [accountDetails]="accountDetails$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      [isAdmin]="isAdmin$ | async"
      [isSeniorAdmin]="isSeniorAdmin$ | async"
      [accountHolder]="(account$ | async).accountHolder"
      [accountType]="(account$ | async).accountType"
      [accountDetailsSameBillingAddress]="
        accountDetailsSameBillingAddress$ | async
      "
      [accountHolderType]="(account$ | async).accountHolder.type"
      [showSalesContact]="showSalesContact$ | async"
      (accountDetailsOutput)="onContinue($event)"
      (copyAccountHolderAddressAccountDetails)="
        onSameAddressCheckChange($event)
      "
      (errorDetails)="onError($event)"
    >
    </app-account-details-form>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditAccountDetailsContainerComponent implements OnInit {
  account$: Observable<Account>;
  accountDetails$: Observable<AccountDetails>;
  countries$: Observable<IUkOfficialCountry[]>;
  countryCodes$: Observable<CountryCodeModel[]>;
  isAdmin$: Observable<boolean>;
  isSeniorAdmin$: Observable<boolean>;
  accountDetailsSameBillingAddress$: Observable<boolean>;
  showSalesContact$: Observable<boolean>;

  readonly billingRoute = AccountDetailsWizardRoutes.BILLING_DETAILS;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit(): void {
    this.account$ = this.store.select(selectAccount);
    this.accountDetails$ = this.store.select(selectAccountDetailsForEdit);
    this.countries$ = this.store.select(selectCountries);
    this.countryCodes$ = this.store.select(selectCountryCodes);
    this.isAdmin$ = this.store.select(isAdmin);
    this.isSeniorAdmin$ = this.store.select(isSeniorAdmin);
    this.accountDetailsSameBillingAddress$ = this.store.select(
      selectAccountDetailsSameBillingAddress
    );
    this.showSalesContact$ = this.account$.pipe(
      map((account) => account.accountType),
      map(
        (type) =>
          type === AccountType.OPERATOR_HOLDING_ACCOUNT ||
          type === AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ||
          type === AccountType.TRADING_ACCOUNT
      )
    );
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get(
          'accountId'
        )}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onError(details: ErrorDetail[]) {
    this.store.dispatch(
      errors({
        errorSummary: {
          errors: details,
        },
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelUpdateAccountDetails({
        currentRoute: this.activatedRoute.snapshot['_routerState'].url,
      })
    );
  }

  onContinue(accountDetails: AccountDetails) {
    this.store.dispatch(updateAccountDetails({ accountDetails }));

    this.account$.pipe(take(1)).subscribe((account) => {
      const isBillable = new IsBillablePipe().transform(account.accountType);
      if (isBillable) {
        this._router.navigate([`/account/${account.identifier}/edit-billing`], {
          skipLocationChange: true,
        });
      } else {
        this._router.navigate([`/account/${account.identifier}/edit-confirm`], {
          skipLocationChange: true,
        });
      }
    });
  }

  onSameAddressCheckChange(event) {
    this.store.dispatch(
      accountDetailsSameBillingAddress({
        accountDetailsSameBillingAddress: event,
      })
    );
  }
}
