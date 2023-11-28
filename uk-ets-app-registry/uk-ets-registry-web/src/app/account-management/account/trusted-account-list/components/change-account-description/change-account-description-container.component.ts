import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import {
  cancelDescriptionChange,
  setDescriptionActionAndNavigateToConfirmAction,
} from '@account-management/account/trusted-account-list/actions/trusted-account-list.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { ActivatedRoute } from '@angular/router';
import { DescriptionUpdateActionState } from '@account-management/account/trusted-account-list/reducers/trusted-account-list.reducer';
import {
  selectAccountFullIdentifier,
  selectAccountTrustedAccountDescription,
} from '@account-management/account/account-details/account.selector';
import { selectDescriptionUpdateAction } from '@trusted-account-list/reducers';

@Component({
  selector: 'app-change-account-description-container',
  template: `<app-change-account-description
    (cancelAccountChangeAction)="onCancel()"
    (updateDescription)="onContinue($event)"
    (errorDetails)="onError($event)"
    [accountId]="accountId"
    [accountFullIdentifier]="accountFullIdentifier$ | async"
    [descriptionValue]="description$ | async"
    [existingDescription]="existingDescription$ | async"
  >
  </app-change-account-description>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeAccountDescriptionContainerComponent implements OnInit {
  accountFullIdentifier$: Observable<string>;
  description$: Observable<DescriptionUpdateActionState>;
  existingDescription$: Observable<string>;

  accountId: string;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.accountId = this.route.snapshot.paramMap.get('accountId');
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.accountId}`,
      })
    );
    this.accountFullIdentifier$ = this.store.select(
      selectAccountFullIdentifier
    );
    this.description$ = this.store.select(selectDescriptionUpdateAction);
    this.existingDescription$ = this.store.select(
      selectAccountTrustedAccountDescription
    );
  }

  onContinue(value: DescriptionUpdateActionState) {
    this.store.dispatch(
      setDescriptionActionAndNavigateToConfirmAction({
        descriptionUpdateActionState: value,
      })
    );
  }

  onCancel() {
    this.store.dispatch(cancelDescriptionChange());
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
