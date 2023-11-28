import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Data } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  cancelClicked,
  setRequestUpdateType
} from '@account-management/account/account-holder-details-wizard/actions/account-holder-details-wizard.action';
import { Observable } from 'rxjs';
import { selectUpdateType } from '@account-management/account/account-holder-details-wizard/reducers';
import { AccountHolderDetailsType } from '@account-management/account/account-holder-details-wizard/model';

@Component({
  selector: 'app-select-account-holder-details-type-container',
  template: `
    <app-select-account-holder-details-type
      [routeData]="routeData$ | async"
      [updateType]="updateType$ | async"
      (selectUpdateType)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-select-account-holder-details-type>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectAccountHolderUpdateTypeContainerComponent implements OnInit {
  routeData$: Observable<Data>;
  updateType$: Observable<AccountHolderDetailsType>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get('accountId')}`
      })
    );
    this.updateType$ = this.store.select(selectUpdateType);
    this.routeData$ = this.route.data;
  }

  onContinue(updateType: AccountHolderDetailsType) {
    this.store.dispatch(setRequestUpdateType({ updateType }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
