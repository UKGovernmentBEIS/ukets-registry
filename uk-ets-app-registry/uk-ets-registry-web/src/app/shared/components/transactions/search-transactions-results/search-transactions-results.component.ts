import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Location } from '@angular/common';
import { Router, RouterStateSnapshot } from '@angular/router';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { ARAccessRights } from '@shared/model/account';
import { Transaction, transactionStatusMap } from '@shared/model/transaction';
import { SearchMode } from '@registry-web/shared/resolvers/search.resolver';
import { MenuItemEnum } from '@account-management/account/account-details/model';
import { Store } from '@ngrx/store';
import { canGoBackToList } from '@shared/shared.action';
import { ApiEnumTypes } from '@registry-web/shared/model';

@Component({
  selector: 'app-search-transactions-results',
  templateUrl: './search-transactions-results.component.html',
})
export class SearchTransactionsResultsComponent {
  @Input() results: Transaction[];
  @Input() sortParameters: SortParameters;
  @Input() goBackToListRoute: string;
  @Input() showRunningBalances: boolean;
  @Input() isSortable = true;
  @Input() isAdmin: boolean;

  @Output() readonly sort = new EventEmitter<SortParameters>();

  lowerPermittedAccessRightToAccount = ARAccessRights.READ_ONLY;
  transactionStatusMap = transactionStatusMap;
  url: string;
  accountMenu = MenuItemEnum.OVERVIEW;
  ApiEnumTypes = ApiEnumTypes;

  constructor(
    private router: Router,
    private location: Location,
    private store: Store
  ) {
    const snapshot: RouterStateSnapshot = router.routerState.snapshot;
    this.url =
      snapshot.url.indexOf('?') > -1 ? snapshot.url.split('?')[0] : null;
  }

  changeLocationState(): void {
    // We need to replace the url with the appropriate param mode when entering
    // a detailed page so to load the stored criteria when returning back to results by clicking on the browser's back button
    if (this.url) {
      this.location.go(this.url, `mode=${SearchMode.LOAD}`);
    }
  }

  goToAccountDetailsAndChangeLocationState(
    ukRegistryIdentifier: number,
    event
  ) {
    event.preventDefault();
    this.changeLocationState();
    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: this.goBackToListRoute,
        extras: {
          skipLocationChange: false,
          queryParams: {
            mode: SearchMode.LOAD,
          },
        },
      })
    );

    this.router.navigate(['/account', ukRegistryIdentifier], {
      queryParams: { selectedSideMenu: MenuItemEnum.OVERVIEW },
    });
  }

  navigateToTransaction(transactionId: string, event): void {
    event.preventDefault();
    this.changeLocationState();
    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: this.goBackToListRoute,
        extras: {
          skipLocationChange: false,
          queryParams: {
            mode: SearchMode.LOAD,
          },
        },
      })
    );

    this.router.navigate(['/transaction-details', transactionId], {
      queryParams: { selectedSideMenu: MenuItemEnum.OVERVIEW },
    });
  }

  formatBalance(balance): number {
    return balance.quantity != null || balance.quantity != undefined
      ? balance.quantity
      : 0;
  }
}
