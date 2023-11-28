import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  AccountHolder,
  AccountHolderType,
  Organisation,
} from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { AccountTransferPathsModel } from '@account-transfer/model';
import { selectAcquiringAccountHolder } from '@account-transfer/store/reducers';
import {
  cancelClicked,
  setAcquiringAccountHolderDetails,
} from '@account-transfer/store/actions/account-transfer.actions';

@Component({
  selector: 'app-acquiring-organisation-details-container',
  template: `
    <app-account-holder-organisation-details
      [isAHUpdateWizard]="false"
      [caption]="'Request account transfer'"
      [header]="'Add the Organisation details'"
      [accountHolder]="accountHolder$ | async"
      (outputUser)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-account-holder-organisation-details>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AcquiringOrganisationDetailsContainerComponent implements OnInit {
  accountHolder$: Observable<AccountHolder>;
  organisation = AccountHolderType.ORGANISATION;

  constructor(private route: ActivatedRoute, private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountTransferPathsModel.BASE_PATH}/${
          AccountTransferPathsModel.SELECT_UPDATE_TYPE
        }`,
      })
    );
    this.accountHolder$ = this.store.select(selectAcquiringAccountHolder);
  }

  onContinue(acquiringOrganisationDetails: Organisation): void {
    this.store.dispatch(
      setAcquiringAccountHolderDetails({
        acquiringOrganisationDetails: {
          name: acquiringOrganisationDetails.details.name,
          registrationNumber:
            acquiringOrganisationDetails.details.registrationNumber,
          noRegistrationNumJustification:
            acquiringOrganisationDetails.details.noRegistrationNumJustification,
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
