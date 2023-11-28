import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AccountHolderContact } from '@shared/model/account';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { canGoBack, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { AccountTransferPathsModel } from '@account-transfer/model';
import { selectAcquiringAccountHolderPrimaryContact } from '@account-transfer/store/reducers';
import {
  cancelClicked,
  setAcquiringAccountHolderPrimaryContactDetails,
} from '@account-transfer/store/actions/account-transfer.actions';

@Component({
  selector: 'app-acquiring-primary-contact-details-container',
  template: ` <app-account-holder-contact-details
      [isAHUpdateWizard]="false"
      [accountHolderContact]="accountHolderContact$ | async"
      [contactType]="contactType"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-contact-details>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>`,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AcquiringPrimaryContactDetailsContainerComponent
  implements OnInit {
  accountHolderContact$: Observable<AccountHolderContact>;
  contactType: string;

  constructor(private route: ActivatedRoute, private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountTransferPathsModel.BASE_PATH}/${
          AccountTransferPathsModel.UPDATE_AH_ADDRESS
        }`,
      })
    );
    this.accountHolderContact$ = this.store.select(
      selectAcquiringAccountHolderPrimaryContact
    );
    this.contactType = ContactType.PRIMARY;
  }

  onContinue(value: AccountHolderContact): void {
    this.store.dispatch(
      setAcquiringAccountHolderPrimaryContactDetails({
        acquiringAccountHolderContactDetails: {
          details: value.details,
        },
      })
    );
  }

  onError(details: ErrorDetail[]): void {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel(): void {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
