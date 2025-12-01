import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Inject,
  OnInit,
} from '@angular/core';
import { Observable } from 'rxjs';
import {
  AccountHolder,
  AccountHolderSelectionType,
  AccountHolderType,
} from '@shared/model/account';
import {
  selectAccountHolder,
  selectAccountHolderList,
  selectAccountHolderSelectionType,
  selectAccountHolderType,
  selectAccountHolderWizardCompleted,
} from '@account-opening/account-holder/account-holder.selector';
import { AuthModel } from '../../../auth/auth.model';
import { selectLoggedInUser } from '../../../auth/auth.selector';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import { AccountHolderWizardRoutes } from '@account-opening/account-holder/account-holder-wizard-properties';
import {
  cleanAccountHolderList,
  fetchAccountHolderById,
  fetchAccountHolderList,
  setAccountHolderSelectionType,
  setAccountHolderType,
} from '@account-opening/account-holder/account-holder.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-account-holder-selection-container',
  template: `
    <app-account-holder-selection
      [caption]="'Add the account holder'"
      [accountHolder]="accountHolder$ | async"
      [accountHolderList]="accountHolderList$ | async"
      [accountHolderType]="accountHolderType$ | async"
      [accountHolderSelectionType]="accountHolderSelectionType$ | async"
      [loggedinUser]="loggedinUser$ | async"
      [searchByNameRequestUrl]="searchByNameRequestUrl"
      (errorDetails)="onError($event)"
      (output)="onContinue($event)"
    ></app-account-holder-selection>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountHolderSelectionContainerComponent implements OnInit {
  readonly previousRoute = AccountHolderWizardRoutes.ACCOUNT_HOLDER_TYPE;
  readonly overviewRoute = AccountHolderWizardRoutes.OVERVIEW;

  searchByNameRequestUrl: string;
  accountHolder$: Observable<AccountHolder>;
  accountHolderList$: Observable<AccountHolder[]>;
  accountHolderType$: Observable<string>;
  accountHolderCompleted$: Observable<boolean>;
  accountHolderSelectionType$: Observable<AccountHolderSelectionType>;
  loggedinUser$: Observable<AuthModel>;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private _router: Router,
    private route: ActivatedRoute,
    private cdref: ChangeDetectorRef,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.previousRoute,
        extras: { skipLocationChange: true },
      })
    );

    this.searchByNameRequestUrl =
      this.ukEtsRegistryApiBaseUrl +
      '/account-holder.get.by-name-or-identifier';
    this.accountHolderSelectionType$ = this.store.select(
      selectAccountHolderSelectionType
    );
    this.loggedinUser$ = this.store.select(selectLoggedInUser);
    this.accountHolderCompleted$ = this.store.select(
      selectAccountHolderWizardCompleted
    );
    this.accountHolderType$ = this.store.select(selectAccountHolderType);
    this.accountHolderList$ = this.store.select(selectAccountHolderList);
    this.accountHolder$ = this.store.select(selectAccountHolder);

    this.accountHolderType$.pipe(take(1)).subscribe((accountHolderType) => {
      if (!accountHolderType) {
        this._router.navigate([this.previousRoute], {
          skipLocationChange: true,
        });
      }
      this.loggedinUser$.pipe(take(1)).subscribe((loggedInUser) => {
        this.store.dispatch(
          fetchAccountHolderList({ holderType: accountHolderType })
        );
      });
    });
    this.accountHolderCompleted$
      .pipe(take(1))
      .subscribe((accountHolderCompleted) => {
        if (accountHolderCompleted) {
          this._router.navigate([this.overviewRoute], {
            skipLocationChange: true,
          });
        }
      });
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onContinue(value: any) {
    this.store.dispatch(
      setAccountHolderSelectionType({
        selectionType: value.accountHolderSelectionType,
      })
    );
    if (value.accountHolderSelectionType === AccountHolderSelectionType.NEW) {
      this.store.dispatch(cleanAccountHolderList());
      this.store.dispatch(
        setAccountHolderType({ holderType: value.accountHolderType })
      );
      const nextRoute =
        value.accountHolderType === AccountHolderType.INDIVIDUAL
          ? AccountHolderWizardRoutes.INDIVIDUAL_DETAILS
          : AccountHolderWizardRoutes.ORGANISATION_DETAILS;
      this._router.navigate([nextRoute], {
        relativeTo: this.route,
        skipLocationChange: true,
      });
    } else {
      this.store.dispatch(
        fetchAccountHolderById({
          id:
            value.accountHolderSelectionType ===
            AccountHolderSelectionType.FROM_LIST
              ? value.selectedIdFromList
              : value.selectedIdFromSearch,
        })
      );
    }
  }
}
