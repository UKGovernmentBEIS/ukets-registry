import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import {
  cancelClicked,
  setNewRules,
} from '@tal-transaction-rules/actions/tal-transaction-rules.actions';
import { TrustedAccountListRules } from '@shared/model/account';
import {
  selectCurrentRules,
  selectUpdatedRules,
} from '@tal-transaction-rules/reducers';

@Component({
  selector: 'app-single-person-surrender-excess-allocation-container',
  template: `
    <app-single-person-surrender-excess-allocation
      [currentRules]="currentRules$ | async"
      [updatedRules]="updatedRules$ | async"
      (selectTransactionRuleOption)="onContinue($event)"
    ></app-single-person-surrender-excess-allocation>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SinglePersonSurrenderExcessAllocationContainerComponent
  implements OnInit
{
  currentRules$: Observable<TrustedAccountListRules>;
  updatedRules$: Observable<TrustedAccountListRules>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
      })
    );
    this.currentRules$ = this.store.select(selectCurrentRules);
    this.updatedRules$ = this.store.select(selectUpdatedRules);
  }

  onContinue(newRules) {
    this.store.dispatch(
      setNewRules({
        newRules,
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
