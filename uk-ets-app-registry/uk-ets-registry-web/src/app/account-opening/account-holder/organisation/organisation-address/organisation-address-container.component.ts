import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectAccountHolder,
  selectAccountHolderExisting,
  selectAccountHolderType,
} from '../../account-holder.selector';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { MainWizardRoutes } from '../../../main-wizard.routes';
import { AccountHolderWizardRoutes } from '../../account-holder-wizard-properties';
import { nextPage } from '../../account-holder.actions';
import { selectAllCountries } from '@shared/shared.selector';
import { take } from 'rxjs/operators';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import {
  AccountHolder,
  AccountHolderType,
  Organisation,
} from '@shared/model/account/account-holder';

@Component({
  selector: 'app-organisation-address-container',
  template: `
    <app-account-holder-organisation-address
      [caption]="'Add the account holder'"
      [header]="'Add the organisation address'"
      [countries]="countries$ | async"
      [accountHolder]="accountHolder$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-organisation-address>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OrganisationAddressContainerComponent implements OnInit {
  accountHolder$: Observable<Organisation> = this.store.select(
    selectAccountHolder
  ) as Observable<Organisation>;
  accountHolderType$: Observable<string> = this.store.select(
    selectAccountHolderType
  );
  accountHolderExisting$: Observable<boolean> = this.store.select(
    selectAccountHolderExisting
  );
  countries$: Observable<IUkOfficialCountry[]> = this.store.select(
    selectAllCountries
  );

  readonly noAccountHolderTypeRoute = MainWizardRoutes.ACCOUNT_TYPE;
  readonly wrongAccountHolderTypeRoute =
    AccountHolderWizardRoutes.INDIVIDUAL_DETAILS;
  readonly previousRoute = AccountHolderWizardRoutes.ORGANISATION_DETAILS;
  readonly nextRoute = AccountHolderWizardRoutes.OVERVIEW;
  readonly overviewRoute = AccountHolderWizardRoutes.OVERVIEW;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.previousRoute,
        extras: { skipLocationChange: true },
      })
    );
    this.store.dispatch(clearErrors());
    this.accountHolderType$.pipe(take(1)).subscribe((accountHolderType) => {
      if (!accountHolderType) {
        this._router.navigate([this.noAccountHolderTypeRoute], {
          skipLocationChange: true,
        });
      } else if (accountHolderType === AccountHolderType.INDIVIDUAL) {
        this._router.navigate([this.wrongAccountHolderTypeRoute], {
          skipLocationChange: true,
        });
      }
    });
    this.accountHolderExisting$.pipe(take(1)).subscribe((existing) => {
      if (existing) {
        this._router.navigate([this.overviewRoute], {
          skipLocationChange: true,
        });
      }
    });
  }

  onContinue(value: AccountHolder) {
    this.store.dispatch(
      nextPage({
        accountHolder: value,
      })
    );
    this._router.navigate([this.nextRoute], { skipLocationChange: true });
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
