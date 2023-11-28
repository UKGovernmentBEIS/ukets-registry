import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { canGoBack } from '@shared/shared.action';
import { selectTransactionReference } from '@issue-allowances/reducers';
import { setTransactionReference } from '@issue-allowances/actions/issue-allowance.actions';

@Component({
  selector: 'app-set-transaction-reference-container',
  template: `
    <app-set-transaction-reference
      [title]="'Propose to issue UK allowances'"
      [reference]="transactionReference$ | async"
      (setTransactionReference)="onContinue($event)"
    ></app-set-transaction-reference>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SetTransactionReferenceContainerComponent implements OnInit {
  transactionReference$: Observable<string>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: 'ets-administration/issue-allowances',
        extras: { skipLocationChange: true },
      })
    );
    this.transactionReference$ = this.store.select(selectTransactionReference);
  }

  onContinue(value: string) {
    this.store.dispatch(
      setTransactionReference({
        reference: value,
      })
    );
  }
}
