import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import {
  AccountHolderDetailsType,
  AccountHolderDetailsWizardPathsModel,
} from '@account-management/account/account-holder-details-wizard/model';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { AccountHolderContact } from '@shared/model/account';
import {
  cancelClicked,
  setAccountHolderContactDetails,
} from '@account-management/account/account-holder-details-wizard/actions/account-holder-details-wizard.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { Observable } from 'rxjs';
import {
  selectUpdatedAlternativeAccountHolderContact,
  selectUpdateType,
} from '@account-management/account/account-holder-details-wizard/reducers';

@Component({
  selector:
    'app-update-account-holder-alternative-primary-contact-details-container',
  template: `
    <app-account-holder-contact-details
      [useUpdateLabel]="useUpdateLabel"
      [isAHUpdateWizard]="true"
      [accountHolderContact]="accountHolderContact$ | async"
      [contactType]="contactType"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-contact-details>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateAlternativePrimaryContactDetailsContainerComponent
  implements OnInit {
  contactType: string;
  accountHolderContact$: Observable<AccountHolderContact>;
  useUpdateLabel = true;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountHolderDetailsWizardPathsModel.BASE_PATH}/${
          AccountHolderDetailsWizardPathsModel.SELECT_UPDATE_TYPE
        }`,
      })
    );
    this.store.select(selectUpdateType).subscribe((type) => {
      if (
        type ===
        AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD
      ) {
        this.useUpdateLabel = false;
      }
    });
    this.accountHolderContact$ = this.store.select(
      selectUpdatedAlternativeAccountHolderContact
    );
    this.contactType = ContactType.ALTERNATIVE;
  }

  onContinue(value: AccountHolderContact) {
    this.store.dispatch(
      setAccountHolderContactDetails({
        accountHolderContactChanged: {
          details: value.details,
        },
        contactType: ContactType.ALTERNATIVE,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
