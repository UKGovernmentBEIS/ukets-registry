import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectTransactionReference } from '@issue-kp-units/reducers';
import { canGoBack } from '@shared/shared.action';
import { IssueKpUnitsRoutePathsModel } from '@issue-kp-units/model';
import { setTransactionReference } from '@issue-kp-units/actions/issue-kp-units.actions';

@Component({
  selector: 'app-set-transaction-reference-container',
  template: `
    <app-set-transaction-reference
      [title]="'Issue KP Units'"
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
        goBackRoute: `/kpadministration/issuekpunits/${IssueKpUnitsRoutePathsModel['select-units']}`,
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
