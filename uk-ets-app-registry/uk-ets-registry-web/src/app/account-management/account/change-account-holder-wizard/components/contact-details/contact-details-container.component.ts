import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
} from '@angular/core';
import { AccountHolderContact } from '@shared/model/account/account-holder-contact';
import { combineLatest, Observable, take } from 'rxjs';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ContactType } from '@shared/model/account-holder-contact-type';
import {
  selectAcquiringAccountHolderContact,
  selectAcquiringAccountHolderType,
  selectReturnToOverview,
} from '@change-account-holder-wizard/store/selectors';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import {
  CHANGE_ACCOUNT_HOLDER_BASE_PATH,
  ChangeAccountHolderWizardPaths,
} from '@change-account-holder-wizard/model';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountHolderType } from '@registry-web/shared/model/account';
import { ChangeAccountHolderTaskMap } from '@change-account-holder-wizard/change-account-holder.task-map';

@Component({
  selector: 'app-contact-details-container',
  template: `
    <app-account-holder-contact-details
      [caption]="map.CAPTION"
      [accountHolderContact]="accountHolderContact$ | async"
      [contactType]="contactType"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    />
    <app-cancel-request-link (goToCancelScreen)="onCancel()" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactDetailsContainerComponent implements OnInit {
  private readonly returnToOverview$ = this.store.select(
    selectReturnToOverview
  );
  private readonly accountHolderType$: Observable<AccountHolderType> =
    this.store.select(selectAcquiringAccountHolderType);

  readonly accountHolderContact$: Observable<AccountHolderContact> =
    this.store.select(selectAcquiringAccountHolderContact);
  readonly contactType = ContactType.PRIMARY;
  readonly map = ChangeAccountHolderTaskMap;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    combineLatest([this.accountHolderType$, this.returnToOverview$])
      .pipe(take(1))
      .subscribe(([accountHolderType, returnToOverview]) => {
        const backStep = returnToOverview
          ? ChangeAccountHolderWizardPaths.OVERVIEW
          : accountHolderType === AccountHolderType.INDIVIDUAL
            ? ChangeAccountHolderWizardPaths.INDIVIDUAL_CONTACT
            : ChangeAccountHolderWizardPaths.ORGANISATION_ADDRESS;

        this.store.dispatch(
          canGoBack({
            goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get('accountId')}/${CHANGE_ACCOUNT_HOLDER_BASE_PATH}/${backStep}`,
            extras: { skipLocationChange: true },
          })
        );
      });

    this.store.dispatch(clearErrors());
  }

  onContinue(value: AccountHolderContact) {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_CONTACT_DETAILS({
        contact: value,
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
