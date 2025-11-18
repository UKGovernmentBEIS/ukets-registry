import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { selectAllCountries } from '@shared/shared.selector';
import { take } from 'rxjs/operators';
import {
  AccountHolder,
  Individual,
  IndividualDetails,
} from '@shared/model/account/account-holder';
import {
  selectReturnToOverview,
  selectAcquiringAccountHolder,
} from '@change-account-holder-wizard/store/selectors';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import {
  CHANGE_ACCOUNT_HOLDER_BASE_PATH,
  ChangeAccountHolderWizardPaths,
} from '@change-account-holder-wizard/model';
import { ChangeAccountHolderTaskMap } from '@change-account-holder-wizard/change-account-holder.task-map';

@Component({
  selector: 'app-change-holder-individual-details-container',
  template: `
    <app-account-holder-individual-details
      [caption]="map.CAPTION"
      header="Add the individual's details"
      [accountHolder]="accountHolder$ | async"
      [countries]="countries$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    />
    <app-cancel-request-link (goToCancelScreen)="onCancel()" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndividualDetailsContainerComponent implements OnInit {
  private readonly returnToOverview$ = this.store.select(
    selectReturnToOverview
  );

  readonly accountHolder$ = this.store.select(
    selectAcquiringAccountHolder
  ) as Observable<Individual>;
  readonly countries$ = this.store.select(selectAllCountries);
  readonly map = ChangeAccountHolderTaskMap;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());

    this.returnToOverview$.pipe(take(1)).subscribe((returnToOverview) => {
      this.store.dispatch(
        canGoBack({
          goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get('accountId')}/${CHANGE_ACCOUNT_HOLDER_BASE_PATH}/${
            returnToOverview
              ? ChangeAccountHolderWizardPaths.OVERVIEW
              : ChangeAccountHolderWizardPaths.SELECT_EXISTING_OR_ADD_NEW
          }`,
          extras: { skipLocationChange: true },
        })
      );
    });
  }

  onContinue(value: AccountHolder) {
    const details = value.details as IndividualDetails;
    this.store.dispatch(
      ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_INDIVIDUAL_DETAILS({
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
    const summary: ErrorSummary = { errors: details };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(ChangeAccountHolderWizardActions.CANCEL({ route }));
  }
}
