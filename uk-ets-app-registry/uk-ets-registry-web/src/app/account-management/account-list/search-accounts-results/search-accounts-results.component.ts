import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router, RouterStateSnapshot } from '@angular/router';
import { Location } from '@angular/common';
import { complianceStatusMap } from '@account-shared/model';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { AccountSearchResult, accountStatusMap } from '../account-list.model';
import { SearchMode } from '@registry-web/shared/resolvers/search.resolver';
import { MenuItemEnum } from '@registry-web/account-management/account/account-details/model';
import { canGoBackToList } from '@registry-web/shared/shared.action';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-search-accounts-results',
  templateUrl: './search-accounts-results.component.html',
})
export class SearchAccountsResultsComponent {
  @Input() results: AccountSearchResult[];
  @Input() isAdmin: boolean;
  @Input() sortParameters: SortParameters;

  @Output() readonly sort = new EventEmitter<SortParameters>();

  complianceStatusMap = complianceStatusMap;
  accountStatusMap = accountStatusMap;
  url: string;

  constructor(
    private router: Router,
    private location: Location,
    private store: Store
  ) {
    const snapshot: RouterStateSnapshot = router.routerState.snapshot;
    this.url =
      snapshot.url.indexOf('?') > -1 ? snapshot.url.split('?')[0] : null;
  }

  onSorting($event: SortParameters) {
    this.sort.emit($event);
  }

  formatBalance(balance: number): number {
    return balance ? balance : 0;
  }

  changeLocationState(): void {
    // We need to replace the url with the appropriate param mode when entering
    // a detailed page so to load the stored criteria when returning back to results by clicking on the browser's back button
    if (this.url) {
      this.location.go(this.url, `mode=${SearchMode.LOAD}`);
    }
  }

  goToDetailsAndChangeLocationState(ukRegistryIdentifier: number, event) {
    event.preventDefault();
    this.changeLocationState();
    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: `/account-list`,
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
}
