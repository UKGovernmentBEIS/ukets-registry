import { Component, Input } from '@angular/core';
import { ActivatedRoute, NavigationExtras } from '@angular/router';
import { Location } from '@angular/common';
import { Store } from '@ngrx/store';
import { accountStatusMap } from '@account-management/account-list/account-list.model';
import { AccountStatusActions } from '@account-management/account/account-status/store/actions';
import { Account, ARAccessRights } from '@shared/model/account';
import {
  navigateTo,
  navigateToTransactionProposal,
} from '@shared/shared.action';
import { getUrlIdentifier } from '@shared/shared.util';
import { SearchMode } from '@shared/resolvers/search.resolver';
import { GoBackNavigationExtras } from '@shared/back-button';

@Component({
  selector: 'app-account-header',
  templateUrl: './account-header.component.html',
  styleUrls: ['../../../../shared/sub-headers/styles/sub-header.scss'],
})
export class AccountHeaderComponent {
  @Input()
  account: Account;
  @Input()
  accountHeaderActionsVisibility: boolean;
  @Input()
  showBackToList: boolean;
  @Input()
  goBackToListRoute: string;
  @Input()
  goBackToListNavigationExtras: GoBackNavigationExtras;
  @Input()
  isReportSuccess: boolean;

  readonly accountStatusMap = accountStatusMap;
  readonly accessRight = ARAccessRights;
  searchMode = SearchMode;

  constructor(
    private activatedRoute: ActivatedRoute,
    private store: Store,
    private location: Location
  ) {}

  goToTransaction() {
    this.store.dispatch(
      navigateToTransactionProposal({
        routeSnapshotUrl: getUrlIdentifier(
          this.activatedRoute.snapshot['_routerState'].url
        ),
      })
    );
  }

  goBackToList(event) {
    event.preventDefault();
    const extras: NavigationExtras = {
      skipLocationChange: this.goBackToListNavigationExtras?.skipLocationChange,
      queryParams: this.goBackToListNavigationExtras?.queryParams,
    };
    this.store.dispatch(
      navigateTo({
        route: this.goBackToListRoute,
        extras,
        queryParams: this.goBackToListNavigationExtras?.queryParams,
      })
    );
  }

  fetchAccountStatusActions() {
    this.store.dispatch(
      AccountStatusActions.fetchLoadAndShowAllowedAccountStatusActions()
    );
  }

  private get accountDetails() {
    return this.account?.accountDetails;
  }

  get accountDetailsNumber() {
    return this.accountDetails?.accountNumber || '';
  }

  get accountDetailsHolderName() {
    return this.accountDetails?.accountHolderName || '';
  }

  get accountDetailsStatus() {
    return this.accountDetails?.accountStatus || '';
  }

  get accountDetailsName() {
    return this.accountDetails?.name || '';
  }
}
