import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectAccountDetails,
  selectAccountDetailsCompleted,
  selectAccountDetailsSameBillingAddress,
} from '../account-details.selector';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { selectAccountType } from '../../account-opening.selector';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { map, take } from 'rxjs/operators';
import { completeWizard, nextPage } from '../account-details.actions';
import { AccountDetailsWizardRoutes } from '../account-details-wizard-properties';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { AccountDetails } from '@shared/model/account/account-details';
import { accountDetailsSameBillingAddress } from '@account-opening/account-opening.actions';
import {
  selectAccountHolder,
  selectAccountHolderType,
} from '../../account-holder/account-holder.selector';
import { AccountHolder, AccountType } from '@shared/model/account';
import { MainWizardRoutes } from '@account-opening/main-wizard.routes';
import { CountryCodeModel } from '@shared/countries/country-code.model';
import { isAdmin, isSeniorAdmin } from '@registry-web/auth/auth.selector';
import { IsBillablePipe } from '@registry-web/shared/pipes';

@Component({
  selector: 'app-account-details-container',
  template: `
    <app-account-details-form
      [accountType]="accountType$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      [isAdmin]="isAdmin$ | async"
      [isSeniorAdmin]="isSeniorAdmin$ | async"
      [accountHolder]="accountHolder$ | async"
      [accountHolderType]="accountHolderType$ | async"
      [accountDetails]="accountDetails$ | async"
      [accountDetailsSameBillingAddress]="
        accountDetailsSameBillingAddress$ | async
      "
      [showSalesContact]="showSalesContact$ | async"
      (accountDetailsOutput)="onContinue($event)"
      (errorDetails)="onError($event)"
      (copyAccountHolderAddressAccountDetails)="
        onSameAddressCheckChange($event)
      "
    >
    </app-account-details-form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountDetailsContainerComponent implements OnInit {
  accountHolderType$: Observable<string> = this.store.select(
    selectAccountHolderType
  ) as Observable<string>;
  accountHolder$: Observable<AccountHolder> = this.store.select(
    selectAccountHolder
  ) as Observable<AccountHolder>;
  accountType$: Observable<AccountType> = this.store.select(selectAccountType);
  accountDetails$: Observable<AccountDetails> = this.store.select(
    selectAccountDetails
  ) as Observable<AccountDetails>;
  accountDetailsCompleted$: Observable<boolean> = this.store.select(
    selectAccountDetailsCompleted
  ) as Observable<boolean>;
  accountDetailsSameBillingAddress$: Observable<boolean> = this.store.select(
    selectAccountDetailsSameBillingAddress
  ) as Observable<boolean>;
  countries$: Observable<IUkOfficialCountry[]> =
    this.store.select(selectAllCountries);
  countryCodes$: Observable<CountryCodeModel[]> =
    this.store.select(selectCountryCodes);
  isAdmin$: Observable<boolean> = this.store.select(isAdmin);
  isSeniorAdmin$: Observable<boolean> = this.store.select(isSeniorAdmin);
  showSalesContact$: Observable<boolean> = this.accountType$.pipe(
    map(
      (type) =>
        type === AccountType.OPERATOR_HOLDING_ACCOUNT ||
        type === AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ||
        type === AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT ||
        type === AccountType.TRADING_ACCOUNT
    )
  );
  accountTypeText: string;

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly billingRoute = AccountDetailsWizardRoutes.BILLING_DETAILS;
  readonly overviewRoute = AccountDetailsWizardRoutes.OVERVIEW;

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.accountType$ = this.store.select(selectAccountType);
    this.accountDetails$ = this.store.select(selectAccountDetails);
    this.accountHolder$ = this.store.select(selectAccountHolder);
    this.accountHolderType$ = this.store.select(selectAccountHolderType);
    this.accountDetailsSameBillingAddress$ = this.store.select(
      selectAccountDetailsSameBillingAddress
    );

    this.accountDetailsCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.overviewRoute,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.mainWizardRoute,
            extras: { skipLocationChange: true },
          })
        );
      }
    });

    this.store.dispatch(completeWizard({ complete: false }));
  }

  onContinue(value: AccountDetails) {
    this.store.dispatch(
      nextPage({
        accountDetails: value,
      })
    );

    this.accountType$.pipe(take(1)).subscribe((accountType) => {
      const isBillable = new IsBillablePipe().transform(accountType);
      if (isBillable) {
        this._router.navigate([this.billingRoute], {
          skipLocationChange: true,
        });
      } else {
        this._router.navigate([this.overviewRoute], {
          skipLocationChange: true,
        });
      }
    });
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
