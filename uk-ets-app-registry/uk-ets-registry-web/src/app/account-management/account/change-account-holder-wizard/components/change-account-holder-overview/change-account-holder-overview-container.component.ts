import {
  ChangeDetectionStrategy,
  Component,
  computed,
  OnInit,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import {
  selectCurrentAccountHolder,
  selectAcquiringAccountHolder,
  selectAcquiringAccountHolderContact,
  selectIsExistingAccountHolder,
  selectAccountHolderDelete,
  selectIsAccountHolderOrphan,
} from '@change-account-holder-wizard/store/selectors';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import {
  CHANGE_ACCOUNT_HOLDER_BASE_PATH,
  ChangeAccountHolderWizardPaths,
} from '@change-account-holder-wizard/model';
import { combineLatest, take } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { ChangeAccountHolderTaskMap } from '../../change-account-holder.task-map';
import { isNil } from '@registry-web/shared/shared.util';

@Component({
  selector: 'app-change-account-holder-overview-container',
  templateUrl: './change-account-holder-overview-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeAccountHolderOverviewContainerComponent implements OnInit {
  readonly transferringAccountHolder$ = this.store.select(
    selectCurrentAccountHolder
  );
  readonly acquiringAccountHolder$ = this.store.select(
    selectAcquiringAccountHolder
  );
  readonly acquiringAccountHolderContact$ = this.store.select(
    selectAcquiringAccountHolderContact
  );
  readonly isExistingAccountHolder$ = this.store.select(
    selectIsExistingAccountHolder
  );

  private readonly isAccountHolderOrphan$ = this.store.select(
    selectIsAccountHolderOrphan
  );
  readonly shouldDeleteTransferringAccountHolder = toSignal(
    this.store.select(selectAccountHolderDelete)
  );
  readonly warningMessage = computed<string | null>(() =>
    this.shouldDeleteTransferringAccountHolder()
      ? ChangeAccountHolderTaskMap.DELETE_ORPHAN_ACCOUNT_HOLDER_WARNING
      : null
  );

  readonly map = ChangeAccountHolderTaskMap;
  readonly isNil = isNil;

  constructor(
    private readonly store: Store,
    private readonly activatedRoute: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.store.dispatch(ChangeAccountHolderWizardActions.INIT_OVERVIEW());

    combineLatest([this.isAccountHolderOrphan$, this.isExistingAccountHolder$])
      .pipe(take(1))
      .subscribe(([isAccountHolderOrphan, isExisting]) => {
        const backRoute = isAccountHolderOrphan
          ? ChangeAccountHolderWizardPaths.DELETE_ORPHAN_ACCOUNT_HOLDER
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

  onSubmit() {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.SUBMIT_CHANGE_ACCOUNT_HOLDER_REQUEST()
    );
  }

  navigateToAccountHolderType() {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.NAVIGATE_TO({
        step: ChangeAccountHolderWizardPaths.SELECT_TYPE,
      })
    );
  }

  navigateToPrimaryContact() {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.NAVIGATE_TO({
        step: ChangeAccountHolderWizardPaths.PRIMARY_CONTACT,
      })
    );
  }

  navigateToDeleteOrphanAccountHolder() {
    this.store.dispatch(
      ChangeAccountHolderWizardActions.NAVIGATE_TO({
        step: ChangeAccountHolderWizardPaths.DELETE_ORPHAN_ACCOUNT_HOLDER,
      })
    );
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(ChangeAccountHolderWizardActions.CANCEL({ route }));
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = { errors: [value] };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
