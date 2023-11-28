import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import {
  cancelClicked,
  setNewRules
} from '@tal-transaction-rules/actions/tal-transaction-rules.actions';
import { FormRadioOption } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import {
  selectCurrentRules,
  selectUpdatedRules
} from '@tal-transaction-rules/reducers';
import { TrustedAccountListRules } from '@shared/model/account';

@Component({
  selector: 'app-transfers-outside-list-container',
  template: `
    <app-transfers-outside-list
      [currentRules]="currentRules$ | async"
      [updatedRules]="updatedRules$ | async"
      (selectTransactionRuleOption)="onContinue($event)"
    ></app-transfers-outside-list>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TransfersOutsideListContainerComponent implements OnInit {
  currentRules$: Observable<TrustedAccountListRules>;
  updatedRules$: Observable<TrustedAccountListRules>;
  updateTypes: FormRadioOption[];

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/tal-transaction-rules/select-second-approval`
      })
    );
    this.currentRules$ = this.store.select(selectCurrentRules);
    this.updatedRules$ = this.store.select(selectUpdatedRules);
  }

  onContinue(newRules: TrustedAccountListRules) {
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
