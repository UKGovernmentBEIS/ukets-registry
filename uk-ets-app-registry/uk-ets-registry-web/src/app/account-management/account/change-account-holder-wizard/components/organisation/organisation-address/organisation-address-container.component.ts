import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectChangeAccountHolderExisting,
  selectChangeAccountHolderType,
  selectAcquiringAccountHolder,
} from '@change-account-holder-wizard/store/reducers';
import { setAccountHolderOrganisationAddress } from '@change-account-holder-wizard/store/actions/change-account-holder-wizard.actions';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { selectAllCountries } from '@shared/shared.selector';
import { take } from 'rxjs/operators';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import {
  AccountHolder,
  AccountHolderType,
  Organisation,
  OrganisationDetails,
} from '@shared/model/account/account-holder';
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model';

@Component({
  selector: 'app-change-holder-organisation-address-container',
  template: `
    <app-account-holder-organisation-address
      [caption]="'Change account holder'"
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
    selectAcquiringAccountHolder
  ) as Observable<Organisation>;
  accountHolderType$: Observable<string> = this.store.select(
    selectChangeAccountHolderType
  );
  accountHolderExisting$: Observable<boolean> = this.store.select(
    selectChangeAccountHolderExisting
  );
  countries$: Observable<IUkOfficialCountry[]> =
    this.store.select(selectAllCountries);

  readonly noAccountHolderTypeRoute =
    ChangeAccountHolderWizardPathsModel.BASE_PATH;
  readonly wrongAccountHolderTypeRoute =
    ChangeAccountHolderWizardPathsModel.INDIVIDUAL_DETAILS;
  readonly previousRoute =
    '/' +
    ChangeAccountHolderWizardPathsModel.BASE_PATH +
    '/' +
    ChangeAccountHolderWizardPathsModel.ORGANISATION_DETAILS;
  readonly overviewRoute =
    '/' +
    ChangeAccountHolderWizardPathsModel.BASE_PATH +
    '/' +
    ChangeAccountHolderWizardPathsModel.CHECK_CHANGE_ACCOUNT_HOLDER;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute:
          `/account/${this.route.snapshot.paramMap.get('accountId')}` +
          this.previousRoute,
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
      setAccountHolderOrganisationAddress({
        address: value.address,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
