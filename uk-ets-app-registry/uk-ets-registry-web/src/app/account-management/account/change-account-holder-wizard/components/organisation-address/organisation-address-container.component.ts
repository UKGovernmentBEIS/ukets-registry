import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectAcquiringAccountHolder,
  selectReturnToOverview,
} from '@change-account-holder-wizard/store/selectors';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { selectAllCountries } from '@shared/shared.selector';
import { take } from 'rxjs/operators';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import {
  AccountHolder,
  Organisation,
} from '@shared/model/account/account-holder';
import {
  CHANGE_ACCOUNT_HOLDER_BASE_PATH,
  ChangeAccountHolderWizardPaths,
} from '@change-account-holder-wizard/model';
import { ChangeAccountHolderTaskMap } from '@change-account-holder-wizard/change-account-holder.task-map';

@Component({
  selector: 'app-change-holder-organisation-address-container',
  template: `
    <app-account-holder-organisation-address
      [caption]="map.CAPTION"
      [header]="'Add the organisation address'"
      [countries]="countries$ | async"
      [accountHolder]="accountHolder$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    />
    <app-cancel-request-link (goToCancelScreen)="onCancel()" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OrganisationAddressContainerComponent implements OnInit {
  private readonly returnToOverview$ = this.store.select(
    selectReturnToOverview
  );

  readonly accountHolder$ = this.store.select(
    selectAcquiringAccountHolder
  ) as Observable<Organisation>;
  readonly countries$ = this.store.select(selectAllCountries);
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
              : ChangeAccountHolderWizardPaths.ORGANISATION_DETAILS
          }`,
          extras: { skipLocationChange: true },
        })
      );
    });

    this.store.dispatch(clearErrors());
  }

  onContinue(value: AccountHolder) {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_ORGANISATION_ADDRESS({
        address: value.address,
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
