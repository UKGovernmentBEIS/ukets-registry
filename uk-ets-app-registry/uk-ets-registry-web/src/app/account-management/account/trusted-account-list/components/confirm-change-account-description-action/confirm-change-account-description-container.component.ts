import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { Observable } from 'rxjs';
import {
  cancelDescriptionChange,
  navigateTo,
  submitChangeDescriptionAction,
} from '@account-management/account/trusted-account-list/actions/trusted-account-list.actions';
import { DescriptionUpdateActionState } from '@account-management/account/trusted-account-list/reducers/trusted-account-list.reducer';
import { selectDescriptionUpdateAction } from '@account-management/account/trusted-account-list/reducers/trusted-account-list.selector';
import { selectAccountFullIdentifier } from '@account-management/account/account-details/account.selector';

@Component({
  selector: 'app-confirm-change-description-action-container',
  template: `
    <app-confirm-change-description
      [descriptionUpdateAction]="descriptionUpdateAction$ | async"
      [accountIdentifier]="accountId"
      [accountFullIdentifier]="accountFullIdentifier$ | async"
      (cancelDescriptionUpdateAction)="onCancel()"
      (description)="onContinue($event)"
      (errorDetails)="onError($event)"
      (navigateToEmitter)="navigateTo($event)"
    >
    </app-confirm-change-description>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmChangeAccountDescriptionContainerComponent
  implements OnInit {
  descriptionUpdateAction$: Observable<DescriptionUpdateActionState>;
  accountFullIdentifier$: Observable<string>;

  accountId: string;
  routePathForDetails: string;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.accountId = this.route.snapshot.paramMap.get('accountId');
    this.accountFullIdentifier$ = this.store.select(
      selectAccountFullIdentifier
    );
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.accountId}/trusted-account-list/change-description`,
        extras: { skipLocationChange: true },
      })
    );
    this.descriptionUpdateAction$ = this.store.select(
      selectDescriptionUpdateAction
    );
  }

  onContinue(descriptionUpdateActionState: DescriptionUpdateActionState) {
    this.store.dispatch(
      submitChangeDescriptionAction({
        descriptionUpdateActionState: descriptionUpdateActionState,
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

  navigateTo(routePath: string) {
    this.store.dispatch(
      navigateTo({
        route: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${routePath}`,
        extras: { skipLocationChange: true },
      })
    );
  }
}
