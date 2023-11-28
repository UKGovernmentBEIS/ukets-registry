import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ActivatedRoute, Data, Router } from '@angular/router';
import {
  selectCurrentRules,
  selectUpdatedRules,
} from '@account-management/account/tal-transaction-rules/reducers';
import { TalTransactionRulesActions } from '@account-management/account/tal-transaction-rules/actions';
import { Observable } from 'rxjs';
import { TalTransactionRulesRoutePaths } from '@account-management/account/tal-transaction-rules/model';
import { TrustedAccountListRules } from '@shared/model/account';
import {
  cancelClicked,
  submitUpdateRequest,
} from '@account-management/account/tal-transaction-rules/actions/tal-transaction-rules.actions';
import { selectIsOHAOrAOHA } from '@account-management/account/account-details/account.selector';

@Component({
  selector: 'app-check-update-request-container',
  template: `
    <app-check-update-request
      [currentRules]="currentRules$ | async"
      [updatedRules]="updatedRules$ | async"
      [isOHAOrAOHA]="isOHAOrAOHA$ | async"
      (updateRequestChecked)="onContinue()"
      (cancel)="onCancel($event)"
      (navigateToEmitter)="onStepBack($event)"
    ></app-check-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckUpdateRequestContainerComponent implements OnInit {
  currentRules$: Observable<TrustedAccountListRules>;
  updatedRules$: Observable<TrustedAccountListRules>;
  isOHAOrAOHA$: Observable<boolean>;

  goBackPath: string;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.route.data.subscribe((data: Data) => {
      this.initData(data);
    });
    this.isOHAOrAOHA$ = this.store.select(selectIsOHAOrAOHA);
    this.currentRules$ = this.store.select(selectCurrentRules);
    this.updatedRules$ = this.store.select(selectUpdatedRules);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/tal-transaction-rules/select-transfers-outside-list`,
        extras: { skipLocationChange: true },
      })
    );
  }

  protected initData(data: Data) {
    this.goBackPath = data.goBackPath;
  }

  onStepBack(step: TalTransactionRulesRoutePaths) {
    this.store.dispatch(
      TalTransactionRulesActions.navigateTo({
        route: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/tal-transaction-rules/${TalTransactionRulesRoutePaths[step]}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onContinue() {
    this.store.dispatch(submitUpdateRequest());
  }

  onCancel(route: string) {
    this.store.dispatch(cancelClicked({ route }));
  }
}
