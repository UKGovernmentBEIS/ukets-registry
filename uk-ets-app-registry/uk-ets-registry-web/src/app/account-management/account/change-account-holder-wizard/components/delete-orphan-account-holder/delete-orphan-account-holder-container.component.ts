import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { ChangeAccountHolderTaskMap } from '@change-account-holder-wizard/change-account-holder.task-map';
import {
  selectAccountHolderDelete,
  selectIsExistingAccountHolder,
  selectReturnToOverview,
} from '@change-account-holder-wizard/store/selectors';
import {
  CHANGE_ACCOUNT_HOLDER_BASE_PATH,
  ChangeAccountHolderWizardPaths,
} from '@change-account-holder-wizard/model';
import { combineLatest, take } from 'rxjs';

@Component({
  selector: 'app-delete-orphan-account-holder-container',
  template: `<app-delete-orphan-account-holder
      [caption]="map.CAPTION"
      [accountHolderDelete]="accountHolderDelete$ | async"
      (valueSelected)="onContinue($event)"
      (errorDetails)="onError($event)"
    /><app-cancel-request-link (goToCancelScreen)="onCancel()" />`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteOrphanAccountHolderContainerComponent implements OnInit {
  private readonly returnToOverview$ = this.store.select(
    selectReturnToOverview
  );
  private readonly isExistingAccountHolder$ = this.store.select(
    selectIsExistingAccountHolder
  );

  readonly accountHolderDelete$ = this.store.select(selectAccountHolderDelete);
  readonly map = ChangeAccountHolderTaskMap;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    combineLatest([this.returnToOverview$, this.isExistingAccountHolder$])
      .pipe(take(1))
      .subscribe(([returnToOverview, isExisting]) => {
        const backRoute = returnToOverview
          ? ChangeAccountHolderWizardPaths.OVERVIEW
          : isExisting
            ? ChangeAccountHolderWizardPaths.SELECT_EXISTING_OR_ADD_NEW
            : ChangeAccountHolderWizardPaths.PRIMARY_CONTACT_WORK;
        this.store.dispatch(
          canGoBack({
            goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get(
              'accountId'
            )}/${CHANGE_ACCOUNT_HOLDER_BASE_PATH}/${backRoute}`,
            extras: { skipLocationChange: true },
          })
        );
      });
  }

  onContinue(accountHolderDelete: boolean) {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.SET_DELETE_ORPHAN_ACCOUNT_HOLDER({
        accountHolderDelete,
      })
    );
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = { errors: value };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(ChangeAccountHolderWizardActions.CANCEL({ route }));
  }
}
