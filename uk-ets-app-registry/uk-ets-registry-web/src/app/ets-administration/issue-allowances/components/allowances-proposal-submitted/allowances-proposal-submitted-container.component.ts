import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { BusinessCheckResult } from '@shared/model/transaction';
import { selectBusinessCheckResult } from '@issue-allowances/reducers';
import { canGoBack } from '@shared/shared.action';

@Component({
  selector: 'app-allowances-proposal-submitted-container',
  template: `
    <app-allowances-proposal-submitted
      [businessCheckResult]="businessCheckResult$ | async"
    >
    </app-allowances-proposal-submitted>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AllowancesProposalSubmittedContainerComponent implements OnInit {
  businessCheckResult$: Observable<BusinessCheckResult>;
  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.businessCheckResult$ = this.store.select(selectBusinessCheckResult);
  }
}
