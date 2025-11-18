import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
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
  Individual,
} from '@shared/model/account/account-holder';
import {
  selectAcquiringAccountHolder,
  selectReturnToOverview,
} from '@change-account-holder-wizard/store/selectors';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import {
  CHANGE_ACCOUNT_HOLDER_BASE_PATH,
  ChangeAccountHolderWizardPaths,
} from '@change-account-holder-wizard/model';
import { ChangeAccountHolderTaskMap } from '@change-account-holder-wizard/change-account-holder.task-map';

@Component({
  selector: 'app-change-holder-individual-contact-details-container',
  template: `
    <app-account-holder-individual-contact-details
      [caption]="map.CAPTION"
      header="Add the individual's contact details"
      [accountHolder]="accountHolder$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      (errorDetails)="onError($event)"
      (output)="onContinue($event)"
    />
    <app-cancel-request-link (goToCancelScreen)="onCancel()" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndividualContactDetailsContainerComponent implements OnInit {
  private readonly returnToOverview$ = this.store.select(
    selectReturnToOverview
  );

  readonly accountHolder$ = this.store.select(
    selectAcquiringAccountHolder
  ) as Observable<Individual>;
  readonly countries$ = this.store.select(selectAllCountries);
  readonly countryCodes$ = this.store.select(selectCountryCodes);

  readonly map = ChangeAccountHolderTaskMap;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.returnToOverview$.pipe(take(1)).subscribe((returnToOverview) => {
      this.store.dispatch(
        canGoBack({
          goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get('accountId')}/${CHANGE_ACCOUNT_HOLDER_BASE_PATH}/${
            returnToOverview
              ? ChangeAccountHolderWizardPaths.OVERVIEW
              : ChangeAccountHolderWizardPaths.INDIVIDUAL_DETAILS
          }`,
          extras: { skipLocationChange: true },
        })
      );
    });

    this.store.dispatch(clearErrors());
  }

  onContinue(value: AccountHolder) {
    const individual = value as Individual;
    this.store.dispatch(
      ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_INDIVIDUAL_CONTACT_DETAILS(
        {
          individualContactDetails: {
            address: value.address,
            phoneNumber: individual.phoneNumber,
            emailAddress: individual.emailAddress,
          },
        }
      )
    );
  }

  onError(value: ErrorDetail[]) {
    const errorSummaries = new ErrorSummary(value);
    this.store.dispatch(errors({ errorSummary: errorSummaries }));
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(ChangeAccountHolderWizardActions.CANCEL({ route }));
  }
}
