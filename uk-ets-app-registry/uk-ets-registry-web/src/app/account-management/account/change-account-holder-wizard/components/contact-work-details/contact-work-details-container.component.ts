import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AccountHolderContact } from '@shared/model/account/account-holder-contact';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { ContactType } from '@shared/model/account-holder-contact-type';
import {
  selectAllCountries,
  selectCountryCodes,
} from '@shared/shared.selector';
import {
  selectAcquiringAccountHolderAddress,
  selectAcquiringAccountHolderContact,
  selectReturnToOverview,
  selectSamePrimaryContactAddress,
} from '@change-account-holder-wizard/store/selectors';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions/';
import {
  CHANGE_ACCOUNT_HOLDER_BASE_PATH,
  ChangeAccountHolderWizardPaths,
} from '@change-account-holder-wizard/model';
import { ChangeAccountHolderTaskMap } from '@change-account-holder-wizard/change-account-holder.task-map';
import { take } from 'rxjs';

@Component({
  selector: 'app-contact-details-container',
  template: `
    <app-account-holder-contact-work-details
      [caption]="map.CAPTION"
      [contactType]="contactType"
      [accountHolderContact]="accountHolderContact$ | async"
      [countries]="countries$ | async"
      [countryCodes]="countryCodes$ | async"
      (errorDetails)="onError($event)"
      [accountHolderAddress]="accountHolderAddress$ | async"
      [sameAddress]="sameAddress$ | async"
      (output)="onContinue($event)"
      (copyAccountHolderAddressToWorkAddress)="onAddressCheckChange($event)"
    />
    <app-cancel-request-link (goToCancelScreen)="onCancel()" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactWorkDetailsContainerComponent implements OnInit {
  private readonly returnToOverview$ = this.store.select(
    selectReturnToOverview
  );
  readonly accountHolderContact$ = this.store.select(
    selectAcquiringAccountHolderContact
  );
  readonly countries$ = this.store.select(selectAllCountries);
  readonly countryCodes$ = this.store.select(selectCountryCodes);
  readonly sameAddress$ = this.store.select(selectSamePrimaryContactAddress);
  readonly accountHolderAddress$ = this.store.select(
    selectAcquiringAccountHolderAddress
  );

  readonly contactType = ContactType.PRIMARY;
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
              : ChangeAccountHolderWizardPaths.PRIMARY_CONTACT
          }`,
          extras: { skipLocationChange: true },
        })
      );
    });

    this.store.dispatch(clearErrors());
  }

  onContinue(value: AccountHolderContact) {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_CONTACT_ADDRESS({
        contact: value,
      })
    );
  }

  onError(value: ErrorDetail[]) {
    const errorSummaries = new ErrorSummary(value);
    this.store.dispatch(errors({ errorSummary: errorSummaries }));
  }

  onAddressCheckChange(event) {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.SET_SAME_ADDRESS_PRIMARY_CONTACT({
        sameAddress: event,
      })
    );
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(ChangeAccountHolderWizardActions.CANCEL({ route }));
  }
}
