import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { selectAllCountries } from '@shared/shared.selector';
import { take } from 'rxjs/operators';
import {
  AccountHolder,
  AccountHolderType,
  Individual,
  IndividualDetails,
} from '@shared/model/account/account-holder';
import {
  selectChangeAccountHolderExisting,
  selectChangeAccountHolderType,
  selectChangeAccountHolderWizardCompleted,
  selectAcquiringAccountHolder,
} from '@change-account-holder-wizard/store/reducers';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model';

@Component({
  selector: 'app-change-holder-individual-details-container',
  template: `
    <app-account-holder-individual-details
      caption="Change account holder"
      header="Add the individual's details"
      [accountHolder]="accountHolder$ | async"
      [countries]="countries$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-individual-details>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndividualDetailsContainerComponent implements OnInit {
  accountHolder$: Observable<Individual> = this.store.select(
    selectAcquiringAccountHolder
  ) as Observable<Individual>;

  accountHolderType$: Observable<string> = this.store.select(
    selectChangeAccountHolderType
  );

  accountHolderExisting$: Observable<boolean> = this.store.select(
    selectChangeAccountHolderExisting
  );

  accountHolderCompleted$: Observable<boolean> = this.store.select(
    selectChangeAccountHolderWizardCompleted
  );

  countries$: Observable<IUkOfficialCountry[]> =
    this.store.select(selectAllCountries);

  readonly noAccountHolderTypeRoute =
    ChangeAccountHolderWizardPathsModel.BASE_PATH;
  readonly wrongAccountHolderTypeRoute =
    ChangeAccountHolderWizardPathsModel.ORGANISATION_DETAILS;
  readonly previousRoute =
    '/' +
    ChangeAccountHolderWizardPathsModel.BASE_PATH +
    '/' +
    ChangeAccountHolderWizardPathsModel.ACCOUNT_HOLDER_SELECTION;
  readonly overviewRoute =
    '/' +
    ChangeAccountHolderWizardPathsModel.BASE_PATH +
    '/' +
    ChangeAccountHolderWizardPathsModel.CHECK_CHANGE_ACCOUNT_HOLDER;

  constructor(
    private _router: Router,
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());

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

    this.accountHolderCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.store.dispatch(
          canGoBack({
            goBackRoute:
              `/account/${this.route.snapshot.paramMap.get('accountId')}` +
              this.overviewRoute,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.store.dispatch(
          canGoBack({
            goBackRoute:
              `/account/${this.route.snapshot.paramMap.get('accountId')}` +
              this.previousRoute,
            extras: { skipLocationChange: true },
          })
        );
      }
    });

    this.store.dispatch(
      ChangeAccountHolderWizardActions.completeWizard({ complete: false })
    );
  }

  onContinue(value: AccountHolder) {
    const details = value.details as IndividualDetails;
    this.store.dispatch(
      ChangeAccountHolderWizardActions.setAccountHolderIndividualDetails({
        details: {
          firstName: details.firstName,
          lastName: details.lastName,
          countryOfBirth: details.countryOfBirth,
          isOverEighteen: details.isOverEighteen,
        },
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
