import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import { AccountHolderDetailsWizardPathsModel } from '@account-management/account/account-holder-details-wizard/model';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AccountHolderContact } from '@shared/model/account';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  cancelClicked,
  setAccountHolderContactDetails,
} from '@account-management/account/account-holder-details-wizard/actions/account-holder-details-wizard.action';
import { Observable } from 'rxjs';
import { selectUpdatedAccountHolderPrimaryContact } from '@account-management/account/account-holder-details-wizard/reducers';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-update-account-holder-primary-contact-details-container',
  template: `
    <app-account-holder-contact-details
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
export class UpdatePrimaryContactDetailsContainerComponent implements OnInit {
  accountHolderContact$: Observable<AccountHolderContact>;
  contactType: string;

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
    this.accountHolderContact$ = this.store.select(
      selectUpdatedAccountHolderPrimaryContact
    );
    this.contactType = ContactType.PRIMARY;
  }

  onContinue(value: AccountHolderContact) {
    this.store.dispatch(
      setAccountHolderContactDetails({
        accountHolderContactChanged: {
          details: value.details,
        },
        contactType: ContactType.PRIMARY,
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
