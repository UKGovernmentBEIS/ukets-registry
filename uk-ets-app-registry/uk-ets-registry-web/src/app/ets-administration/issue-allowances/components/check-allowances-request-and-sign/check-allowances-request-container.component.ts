import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AcquiringAccountInfo,
  AllowanceTransactionBlockSummary,
  TransactionSummary,
} from '@shared/model/transaction';
import { Store } from '@ngrx/store';
import {
  selectAcquiringAccountInfo,
  selectAllowanceBlocks,
  selectEnrichedAllowanceTransactionSummaryReadyForSigning,
  selectTransactionReference,
} from '@issue-allowances/reducers';

import { canGoBack } from '@shared/shared.action';
import { SignatureInfo } from '@signing/model';
import {
  selectSignature,
  selectSignatureExists,
} from '@signing/reducers/signing.selectors';
import { IssueAllowanceActions } from '@issue-allowances/actions';

@Component({
  selector: 'app-check-allowances-request-container',
  template: `
    <app-check-allowances-request
      [transactionBlockSummaries]="allowanceTransactionBlockSummaries$ | async"
      [acquiringAccountInfo]="acquiringAccountInfo$ | async"
      [transactionId]="
        (selectedTransactionSummaryForSigning$ | async).identifier
      "
      [transactionReference]="transactionReference$ | async"
      (otpCodeEmitter)="onSubmitOtpCode($event)"
    ></app-check-allowances-request>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckAllowancesRequestContainerComponent implements OnInit {
  allowanceTransactionBlockSummaries$: Observable<
    Partial<AllowanceTransactionBlockSummary>[]
  >;

  acquiringAccountInfo$: Observable<AcquiringAccountInfo>;

  selectedTransactionSummaryForSigning$: Observable<TransactionSummary>;

  isProposalSigned$: Observable<boolean>;

  signature$: Observable<SignatureInfo>;

  transactionReference$: Observable<string>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute:
          'ets-administration/issue-allowances/set-transaction-reference',
        extras: { skipLocationChange: true },
      })
    );

    this.allowanceTransactionBlockSummaries$ = this.store.select(
      selectAllowanceBlocks
    );
    this.acquiringAccountInfo$ = this.store.select(selectAcquiringAccountInfo);
    this.selectedTransactionSummaryForSigning$ = this.store.select(
      selectEnrichedAllowanceTransactionSummaryReadyForSigning
    );

    this.isProposalSigned$ = this.store.select(selectSignatureExists);
    this.signature$ = this.store.select(selectSignature);
    this.transactionReference$ = this.store.select(selectTransactionReference);
  }

  onSubmitOtpCode(otpCode: string) {
    this.store.dispatch(IssueAllowanceActions.submitOtpCode({ otpCode }));
  }

  onCancel() {
    this.store.dispatch(
      IssueAllowanceActions.cancelAllowanceProposalRequested()
    );
  }
}
