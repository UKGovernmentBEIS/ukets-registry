import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import { take } from 'rxjs/operators';
import {
  AccountHolder,
  AccountHolderType,
  Individual,
} from '@shared/model/account/account-holder';
import {
  selectChangeAccountHolderExisting,
  selectChangeAccountHolderType,
  selectAcquiringAccountHolder,
} from '@change-account-holder-wizard/store/reducers';
import { setAccountHolderIndividualContactDetails } from '@change-account-holder-wizard/store/actions/change-account-holder-wizard.actions';
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model';

@Component({
  selector: 'app-change-holder-individual-contact-details-container',
  template: `
    <app-account-holder-individual-contact-details
      caption="Change account holder"
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
  accountHolder$: Observable<Individual> = this.store.select(
    selectAcquiringAccountHolder
  ) as Observable<Individual>;

  accountHolderType$: Observable<string> = this.store.select(
    selectChangeAccountHolderType
  ) as Observable<string>;

  accountHolderExisting$: Observable<boolean> = this.store.select(
    selectChangeAccountHolderExisting
  );
  countries$: Observable<IUkOfficialCountry[]>;
  countryCodes$: Observable<
    {
      region: string;
      code: string;
    }[]
  >;

  readonly noAccountHolderTypeRoute =
    ChangeAccountHolderWizardPathsModel.BASE_PATH;
  readonly wrongAccountHolderTypeRoute =
    ChangeAccountHolderWizardPathsModel.ORGANISATION_DETAILS;
  readonly previousRoute =
    '/' +
    ChangeAccountHolderWizardPathsModel.BASE_PATH +
    '/' +
    ChangeAccountHolderWizardPathsModel.INDIVIDUAL_DETAILS;
  readonly overviewRoute = 'AccountHolderWizardRoutes.OVERVIEW';

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    // TODO: should routing happen at this point?
    this.store.dispatch(
      canGoBack({
        goBackRoute:
          `/account/${this.route.snapshot.paramMap.get('accountId')}` +
          this.previousRoute,
      })
    );
    this.store.dispatch(clearErrors());

    this.accountHolder$ = this.store.select(
      selectAcquiringAccountHolder
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
    const individual = value as Individual;
    this.store.dispatch(
      setAccountHolderIndividualContactDetails({
        contactDetails: {
          address: value.address,
          phoneNumber: individual.phoneNumber,
          emailAddress: individual.emailAddress,
        },
      })
    );
  }

  onError(value: ErrorDetail[]) {
    const errorSummaries = new ErrorSummary(value);
    this.store.dispatch(errors({ errorSummary: errorSummaries }));
  }
}
