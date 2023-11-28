import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack, errors } from 'src/app/shared/shared.action';
import {
  selectAcquiringAccountsForCommitmentPeriod,
  selectCommitmentPeriodList,
  selectSelectedAcquiringAccountForCommitmentPeriod,
  selectSelectedCommitmentPeriod
} from '../../reducers';
import { Observable } from 'rxjs';
import * as IssueKpUnitActions from '../../actions/issue-kp-units.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AccountInfo, CommitmentPeriod } from '@shared/model/transaction';

@Component({
  selector: 'app-uk-kp-administration-issue-units-container',
  template: `
    <app-uk-kp-administration-issue-units
      [commitmentPeriodList]="commitmentPeriodList$ | async"
      [acquiringAccountListForCommitmentPeriod]="acquiringAccountList$ | async"
      [selectedCommitmentPeriod]="selectedCommitmentPeriod$ | async"
      [selectedAcquiringAccountInfo]="selectedAcquiringAccount$ | async"
      (selectedCommitmentPeriodChanged)="onCommitmentPeriodChange($event)"
      (errorDetails)="onErrors($event)"
      (selectedAcquiringAccountIdentifier)="onContinue($event)"
    >
    </app-uk-kp-administration-issue-units>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectCommitmentPeriodAccountContainerComponent implements OnInit {
  commitmentPeriodList$: Observable<CommitmentPeriod[]>;
  acquiringAccountList$: Observable<AccountInfo[]>;

  selectedCommitmentPeriod$: Observable<CommitmentPeriod>;
  selectedAcquiringAccount$: Observable<AccountInfo>;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.commitmentPeriodList$ = this.store.select(selectCommitmentPeriodList);
    this.acquiringAccountList$ = this.store.select(
      selectAcquiringAccountsForCommitmentPeriod
    );
    this.selectedAcquiringAccount$ = this.store.select(
      selectSelectedAcquiringAccountForCommitmentPeriod
    );
    this.selectedCommitmentPeriod$ = this.store.select(
      selectSelectedCommitmentPeriod
    );
  }

  onCommitmentPeriodChange(commitmentPeriod: CommitmentPeriod) {
    this.store.dispatch(
      IssueKpUnitActions.loadAccountsForCommitmentPeriod({ commitmentPeriod })
    );
  }

  onErrors(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onContinue(selectedAcquiringAccountIdentifier: number) {
    this.store.dispatch(
      IssueKpUnitActions.selectAcquiringAccount({
        selectedAcquiringAccountIdentifier
      })
    );
  }
}
