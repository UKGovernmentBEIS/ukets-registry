import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import {
  selectTrustedAccountsEligibleForRemoval,
  trustedAccountsForRemoval
} from '@trusted-account-list/reducers';
import {
  cancelClicked,
  selectTrustedAccountsForRemoval
} from '@trusted-account-list/actions/trusted-account-list.actions';
import { TrustedAccount } from '@shared/model/account';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-remove-account-container',
  template: `
    <app-remove-account
      [eligibleTrustedAccounts]="trustedAccounts$ | async"
      [selectedAccountsForRemoval]="selectedTrustedAccountsForRemoval$ | async"
      (selectTrustedAccountsForRemoval)="onContinue($event)"
      (cancel)="onCancel($event)"
    ></app-remove-account>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RemoveAccountContainerComponent implements OnInit {
  trustedAccounts$: Observable<TrustedAccount[]>;
  selectedTrustedAccountsForRemoval$: Observable<TrustedAccount[]>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.trustedAccounts$ = this.store.select(
      selectTrustedAccountsEligibleForRemoval
    );
    this.selectedTrustedAccountsForRemoval$ = this.store.select(
      trustedAccountsForRemoval
    );
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/trusted-account-list`
      })
    );
  }

  onContinue(selectedTrustedAccountsForRemoval: TrustedAccount[]) {
    this.store.dispatch(
      selectTrustedAccountsForRemoval({
        trustedAccountForRemoval: selectedTrustedAccountsForRemoval
      })
    );
  }

  onCancel(route: string) {
    this.store.dispatch(cancelClicked({ route }));
  }
}
