import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import {
  cancelClicked,
  setNewRules
} from '@tal-transaction-rules/actions/tal-transaction-rules.actions';
import { TrustedAccountListRules } from '@shared/model/account';
import {
  selectCurrentRules,
  selectUpdatedRules
} from '@tal-transaction-rules/reducers';

@Component({
  selector: 'app-second-approval-necessary-container',
  template: `
    <app-second-approval-necessary
      [currentRules]="currentRules$ | async"
      [updatedRules]="updatedRules$ | async"
      (selectTransactionRuleOption)="onContinue($event)"
    ></app-second-approval-necessary>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SecondApprovalNecessaryContainerComponent implements OnInit {
  currentRules$: Observable<TrustedAccountListRules>;
  updatedRules$: Observable<TrustedAccountListRules>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get('accountId')}`
      })
    );
    this.currentRules$ = this.store.select(selectCurrentRules);
    this.updatedRules$ = this.store.select(selectUpdatedRules);
  }

  onContinue(newRules) {
    this.store.dispatch(
      setNewRules({
        newRules
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
