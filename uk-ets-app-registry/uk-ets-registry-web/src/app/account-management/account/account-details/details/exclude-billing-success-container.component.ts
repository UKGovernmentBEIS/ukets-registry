import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Account } from '@shared/model/account';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';

@Component({
  selector: 'app-exclude-billing-success-container',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <div class="govuk-panel govuk-panel--confirmation">
          <h1 class="govuk-panel__title">
            The account has been excluded from billing.
          </h1>
        </div>
      </div>
    </div>
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <p class="govuk-body">
          <a
            [routerLink]="['/account/' + (account$ | async).identifier]"
            class="govuk-link govuk-link--no-visited-state"
            >Back to the account</a
          >
        </p>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExcludeBillingSucessContainerComponent implements OnInit {
  account$: Observable<Account>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.account$ = this.store.select(selectAccount);
    this.store.dispatch(
      canGoBack({
        goBackRoute: null,
      })
    );
  }
}
