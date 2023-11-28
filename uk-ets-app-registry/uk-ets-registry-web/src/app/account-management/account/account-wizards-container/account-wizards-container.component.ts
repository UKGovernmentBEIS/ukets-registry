import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Account } from '@shared/model/account';
import { selectAccount } from '@account-management/account/account-details/account.selector';

@Component({
  selector: 'app-account-wizards-container',
  template: `
    <app-feature-header-wrapper>
      <app-account-header
        [account]="account$ | async"
        [accountHeaderActionsVisibility]="false"
        [showBackToList]="false"
      >
      </app-account-header>
    </app-feature-header-wrapper>
    <router-outlet></router-outlet>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountWizardsContainerComponent implements OnInit {
  account$: Observable<Account>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.account$ = this.store.select(selectAccount);
  }
}
