import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AccountHolderContact } from '@shared/model/account/account-holder-contact';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { ContactType } from '@shared/model/account-holder-contact-type';
import {
  selectAcquiringAccountHolderContact,
  selectChangeAccountHolderType,
} from '@change-account-holder-wizard/store/reducers';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model';
import { ActivatedRoute } from '@angular/router';
import { AccountHolderType } from '@registry-web/shared/model/account';

@Component({
  selector: 'app-contact-details-container',
  template: `
    <app-account-holder-contact-details
      [accountHolderContact]="accountHolderContact$ | async"
      [contactType]="contactType"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-contact-details>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactDetailsContainerComponent implements OnInit {
  accountHolderContact$: Observable<AccountHolderContact> = this.store.select(
    selectAcquiringAccountHolderContact
  );

  accountHolderType$: Observable<AccountHolderType> = this.store.select(
    selectChangeAccountHolderType
  );

  readonly contactType = ContactType.PRIMARY;

  constructor(
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.accountHolderType$.subscribe((accountHolderType) => {
      if (accountHolderType === AccountHolderType.INDIVIDUAL) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: `/account/${this.route.snapshot.paramMap.get(
              'accountId'
            )}/${ChangeAccountHolderWizardPathsModel.BASE_PATH}/${ChangeAccountHolderWizardPathsModel.INDIVIDUAL_CONTACT_DETAILS}`,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.store.dispatch(
          canGoBack({
            goBackRoute: `/account/${this.route.snapshot.paramMap.get(
              'accountId'
            )}/${ChangeAccountHolderWizardPathsModel.BASE_PATH}/${ChangeAccountHolderWizardPathsModel.ORGANISATION_ADDRESS_DETAILS}`,
            extras: { skipLocationChange: true },
          })
        );
      }
    });

    this.store.dispatch(clearErrors());
  }

  onContinue(value: AccountHolderContact) {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.setAccountHolderContactDetails({
        contact: value,
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
