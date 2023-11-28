import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Location } from '@angular/common';
import { Router, RouterStateSnapshot } from '@angular/router';
import { UserProjection } from '../user-list.model';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { userStatusMap } from '@shared/user';
import { SearchMode } from '@shared/resolvers/search.resolver';
import { Store } from '@ngrx/store';
import { canGoBackToList } from '@shared/shared.action';

@Component({
  selector: 'app-search-users-results',
  templateUrl: './search-users-results.component.html',
})
export class SearchUsersResultsComponent {
  @Input() results: UserProjection[];
  @Input() sortParameters: SortParameters;

  @Output() readonly sort = new EventEmitter<SortParameters>();
  dateFormat = 'dd MMM yyyy';
  userStatusMap = userStatusMap;
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

  changeLocationState(): void {
    // We need to replace the url with the appropriate param mode when entering
    // a detailed page so to load the stored criteria when returning back to results by pressing the browser's back button
    if (this.url) {
      this.location.go(this.url, `mode=${SearchMode.LOAD}`);
    }
  }

  goToUserDetailsAndChangeLocationState(userId: string, event) {
    event.preventDefault();
    this.changeLocationState();
    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: `/user-list`,
        extras: {
          skipLocationChange: false,
          queryParams: {
            mode: SearchMode.LOAD,
          },
        },
      })
    );

    this.router.navigate(['/user-details', userId]);
  }
}
