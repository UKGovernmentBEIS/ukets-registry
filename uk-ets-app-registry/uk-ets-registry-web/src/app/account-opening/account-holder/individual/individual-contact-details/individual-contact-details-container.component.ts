import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  selectAccountHolder,
  selectAccountHolderExisting,
  selectAccountHolderType,
} from '../../account-holder.selector';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { nextPage } from '../../account-holder.actions';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { MainWizardRoutes } from '../../../main-wizard.routes';
import { AccountHolderWizardRoutes } from '../../account-holder-wizard-properties';
import { take } from 'rxjs/operators';
import {
  AccountHolder,
  AccountHolderType,
  Individual,
} from '@shared/model/account/account-holder';

@Component({
  selector: 'app-individual-contact-details-container',
  template: `
    <app-account-holder-individual-contact-details
      caption="Add the account holder"
      header="Add the individual's contact details"
      [accountHolder]="accountHolder$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      (errorDetails)="onError($event)"
      (output)="onContinue($event)"
    ></app-account-holder-individual-contact-details>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndividualContactDetailsContainerComponent implements OnInit {
  accountHolder$: Observable<Individual>;
  accountHolderType$: Observable<string> = this.store.select(
    selectAccountHolderType
  ) as Observable<string>;
  accountHolderExisting$: Observable<boolean> = this.store.select(
    selectAccountHolderExisting
  );
  countries$: Observable<IUkOfficialCountry[]>;
  countryCodes$: Observable<
    {
      region: string;
      code: string;
    }[]
  >;

  readonly noAccountHolderTypeRoute = MainWizardRoutes.ACCOUNT_TYPE;
  readonly wrongAccountHolderTypeRoute =
    AccountHolderWizardRoutes.ORGANISATION_DETAILS;
  readonly previousRoute = AccountHolderWizardRoutes.INDIVIDUAL_DETAILS;
  readonly nextRoute = AccountHolderWizardRoutes.OVERVIEW;
  readonly overviewRoute = AccountHolderWizardRoutes.OVERVIEW;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    // TODO: should routing happen at this point?
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.previousRoute,
        extras: { skipLocationChange: true },
      })
    );
    this.store.dispatch(clearErrors());

    this.accountHolder$ = this.store.select(
      selectAccountHolder
    ) as Observable<Individual>;
    this.countries$ = this.store.select(selectAllCountries);
    this.countryCodes$ = this.store.select(selectCountryCodes);

    this.accountHolderType$.pipe(take(1)).subscribe((accountHolderType) => {
      if (!accountHolderType) {
        this._router.navigate([this.noAccountHolderTypeRoute], {
          skipLocationChange: true,
        });
      } else if (accountHolderType === AccountHolderType.ORGANISATION) {
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

  onError(value: ErrorDetail[]) {
    const errorSummaries = new ErrorSummary(value);
    this.store.dispatch(errors({ errorSummary: errorSummaries }));
  }
}
