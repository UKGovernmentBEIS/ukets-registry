import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AccountHolderType } from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import {
  selectAcquiringAccountHolderType,
  selectReturnToOverview,
} from '@change-account-holder-wizard/store/selectors';
import { ChangeAccountHolderTaskMap } from '@change-account-holder-wizard/change-account-holder.task-map';
import { take } from 'rxjs';
import {
  CHANGE_ACCOUNT_HOLDER_BASE_PATH,
  ChangeAccountHolderWizardPaths,
} from '@change-account-holder-wizard/model';

@Component({
  selector: 'app-change-account-holder-type-container',
  template: `
    <app-account-holder-type
      [caption]="map.CAPTION"
      [accountHolderType]="accountHolderType$ | async"
      (selectedAccountHolderType)="onContinue($event)"
      (errorDetails)="onError($event)"
    />
    <app-cancel-request-link (goToCancelScreen)="onCancel()" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeAccountHolderTypeContainerComponent implements OnInit {
  private readonly returnToOverview$ = this.store.select(
    selectReturnToOverview
  );
  readonly accountHolderType$ = this.store.select(
    selectAcquiringAccountHolderType
  );
  readonly map = ChangeAccountHolderTaskMap;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.returnToOverview$.pipe(take(1)).subscribe((returnToOverview) => {
      const goBackRoute = `/account/${this.activatedRoute.snapshot.paramMap.get('accountId')}`;
      this.store.dispatch(
        canGoBack(
          returnToOverview
            ? {
                goBackRoute: `${goBackRoute}/${CHANGE_ACCOUNT_HOLDER_BASE_PATH}/${
                  ChangeAccountHolderWizardPaths.OVERVIEW
                }`,
                extras: { skipLocationChange: true },
              }
            : { goBackRoute }
        )
      );
    });
  }

  onContinue(value: AccountHolderType) {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_TYPE({
        holderType: value,
      })
    );
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(ChangeAccountHolderWizardActions.CANCEL({ route }));
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = { errors: value };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
