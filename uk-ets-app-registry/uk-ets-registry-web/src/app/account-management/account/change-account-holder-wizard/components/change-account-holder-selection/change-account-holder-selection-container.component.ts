import {
  ChangeDetectionStrategy,
  Component,
  Inject,
  OnInit,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AccountHolderSelectionType } from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import { take } from 'rxjs/operators';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import {
  CHANGE_ACCOUNT_HOLDER_BASE_PATH,
  ChangeAccountHolderWizardPaths,
} from '@change-account-holder-wizard/model';
import {
  selectAccountHolderList,
  selectChangeAccountHolderSelectionType,
  selectAcquiringAccountHolderType,
  selectReturnToOverview,
  selectAcquiringAccountHolder,
} from '@change-account-holder-wizard/store/selectors';
import { ChangeAccountHolderTaskMap } from '@change-account-holder-wizard/change-account-holder.task-map';

@Component({
  selector: 'app-change-account-holder-selection-container',
  template: `
    <app-account-holder-selection
      [caption]="map.CAPTION"
      [accountHolder]="acquiringAccountHolder$ | async"
      [accountHolderList]="accountHolderList$ | async"
      [accountHolderType]="accountHolderType$ | async"
      [accountHolderSelectionType]="accountHolderSelectionType$ | async"
      [loggedinUser]="loggedinUser$ | async"
      [searchByNameRequestUrl]="searchByNameRequestUrl"
      (errorDetails)="onError($event)"
      (output)="onContinue($event)"
    />
    <app-cancel-request-link (goToCancelScreen)="onCancel()" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeAccountHolderSelectionContainerComponent implements OnInit {
  private readonly returnToOverview$ = this.store.select(
    selectReturnToOverview
  );

  readonly searchByNameRequestUrl =
    this.ukEtsRegistryApiBaseUrl + '/account-holder.get.by-name-or-identifier';

  readonly accountHolderSelectionType$ = this.store.select(
    selectChangeAccountHolderSelectionType
  );
  readonly loggedinUser$ = this.store.select(selectLoggedInUser);
  readonly accountHolderType$ = this.store.select(
    selectAcquiringAccountHolderType
  );
  readonly accountHolderList$ = this.store.select(selectAccountHolderList);
  readonly acquiringAccountHolder$ = this.store.select(
    selectAcquiringAccountHolder
  );
  readonly map = ChangeAccountHolderTaskMap;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.returnToOverview$.pipe(take(1)).subscribe((returnToOverview) => {
      this.store.dispatch(
        canGoBack({
          goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get('accountId')}/${CHANGE_ACCOUNT_HOLDER_BASE_PATH}/${
            returnToOverview
              ? ChangeAccountHolderWizardPaths.OVERVIEW
              : ChangeAccountHolderWizardPaths.SELECT_TYPE
          }`,
          extras: { skipLocationChange: true },
        })
      );
    });

    this.accountHolderType$.pipe(take(1)).subscribe((accountHolderType) => {
      this.store.dispatch(
        ChangeAccountHolderWizardActions.FETCH_ACCOUNT_HOLDER_LIST({
          holderType: accountHolderType,
        })
      );
    });
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = { errors: value };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onContinue({
    selectedIdFromSearch,
    selectedIdFromList,
    accountHolderSelectionType,
  }) {
    let accountHolderId = null;

    if (accountHolderSelectionType === AccountHolderSelectionType.FROM_LIST) {
      accountHolderId = selectedIdFromList;
    } else if (
      accountHolderSelectionType === AccountHolderSelectionType.FROM_SEARCH
    ) {
      accountHolderId = selectedIdFromSearch;
    }

    this.store.dispatch(
      ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_SELECTION_TYPE({
        selectionType: accountHolderSelectionType,
        id: accountHolderId,
      })
    );
    if (accountHolderSelectionType === AccountHolderSelectionType.NEW) {
      this.store.dispatch(
        ChangeAccountHolderWizardActions.CLEAN_ACCOUNT_HOLDER_LIST()
      );
    }
  }

  onCancel() {
    const route = this.router.url;
    this.store.dispatch(ChangeAccountHolderWizardActions.CANCEL({ route }));
  }
}
