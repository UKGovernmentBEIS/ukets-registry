import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { take } from 'rxjs/operators';
import {
  AccountHolder,
  AccountHolderType,
  Organisation,
  OrganisationDetails,
} from '@shared/model/account/account-holder';
import {
  selectChangeAccountHolderExisting,
  selectChangeAccountHolderWizardCompleted,
  selectAcquiringAccountHolder,
  selectChangeAccountHolderType,
} from '@change-account-holder-wizard/store/reducers';
import {
  completeWizard,
  setAccountHolderOrganisationDetails,
} from '@change-account-holder-wizard/store/actions/change-account-holder-wizard.actions';
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model';

@Component({
  selector: 'app-change-holder-organisation-details-container',
  template: `
    <app-account-holder-organisation-details
      [caption]="'Change account holder'"
      [header]="'Add the organisation details'"
      [accountHolder]="accountHolder$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-organisation-details>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OrganisationDetailsContainerComponent implements OnInit {
  accountHolder$: Observable<Organisation> = this.store.select(
    selectAcquiringAccountHolder
  ) as Observable<Organisation>;

  accountHolderType$: Observable<string> = this.store.select(
    selectChangeAccountHolderType
  );

  accountHolderExisting$: Observable<boolean> = this.store.select(
    selectChangeAccountHolderExisting
  );

  accountHolderCompleted$: Observable<boolean> = this.store.select(
    selectChangeAccountHolderWizardCompleted
  );

  readonly noAccountHolderTypeRoute =
    ChangeAccountHolderWizardPathsModel.BASE_PATH;
  readonly wrongAccountHolderTypeRoute =
    ChangeAccountHolderWizardPathsModel.INDIVIDUAL_DETAILS;
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
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.accountHolderType$ = this.store.select(selectChangeAccountHolderType);

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
    this.store.dispatch(completeWizard({ complete: false }));
  }

  onContinue(value: AccountHolder) {
    const details = value.details as OrganisationDetails;
    this.store.dispatch(
      setAccountHolderOrganisationDetails({
        details: {
          name: details.name,
          registrationNumber: details.registrationNumber,
          noRegistrationNumJustification:
            details.noRegistrationNumJustification,
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
