import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  selectEnrichedTransactionSummaryReadyForSigning,
  selectSelectedAcquiringAccountForCommitmentPeriod,
  selectSelectedCommitmentPeriod,
  selectSelectedQuantity,
  selectSelectedRegistryLevel,
  selectTransactionReference,
} from '../../reducers';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import {
  AccountInfo,
  CommitmentPeriod,
  RegistryLevelInfo,
  TransactionSummary,
} from '@shared/model/transaction';
import {
  selectSignature,
  selectSignatureExists,
} from '@signing/reducers/signing.selectors';
import { SignatureInfo } from '@signing/model';
import {
  IssueKpUnitsActions,
  IssueKpUnitsNavigationActions,
} from '@issue-kp-units/actions';
import { IssueKpUnitsRoutePathsModel } from '@issue-kp-units/model';

@Component({
  selector: 'app-check-request-and-submit-container',
  template: `
    <app-check-request-and-submit
      [selectedAcquiringAccount]="selectedAcquiringAccount$ | async"
      [selectedQuantity]="selectedQuantity$ | async"
      [selectedRegistryLevel]="selectedRegistryLevel$ | async"
      [selectedCommitmentPeriod]="selectedCommitmentPeriod$ | async"
      [transactionId]="
        (selectedTransactionSummaryForSigning$ | async).identifier
      "
      [transactionReference]="transactionReference$ | async"
      (otpCodeEmitter)="onSubmitOtpCode($event)"
      (navigateToEmitter)="onStepBack($event)"
    >
    </app-check-request-and-submit>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckRequestAndSignContainerComponent implements OnInit {
  selectedCommitmentPeriod$: Observable<CommitmentPeriod>;
  selectedAcquiringAccount$: Observable<AccountInfo>;
  selectedRegistryLevel$: Observable<RegistryLevelInfo>;
  selectedQuantity$: Observable<number>;
  selectedTransactionSummaryForSigning$: Observable<TransactionSummary>;
  isProposalSigned$: Observable<boolean>;
  signature$: Observable<SignatureInfo>;
  transactionReference$: Observable<string>;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/kpadministration/issuekpunits/${IssueKpUnitsRoutePathsModel['set-transaction-reference']}`,
        extras: { skipLocationChange: true },
      })
    );
    this.selectedCommitmentPeriod$ = this.store.select(
      selectSelectedCommitmentPeriod
    );
    this.selectedAcquiringAccount$ = this.store.select(
      selectSelectedAcquiringAccountForCommitmentPeriod
    );
    this.selectedRegistryLevel$ = this.store.select(
      selectSelectedRegistryLevel
    );
    this.selectedQuantity$ = this.store.select(selectSelectedQuantity);
    this.selectedTransactionSummaryForSigning$ = this.store.select(
      selectEnrichedTransactionSummaryReadyForSigning
    );
    this.isProposalSigned$ = this.store.select(selectSignatureExists);
    this.signature$ = this.store.select(selectSignature);
    this.transactionReference$ = this.store.select(selectTransactionReference);
  }

  onSubmitOtpCode(otpCode: string) {
    this.store.dispatch(IssueKpUnitsActions.submitOtpCode({ otpCode }));
  }

  onStepBack(step: IssueKpUnitsRoutePathsModel) {
    switch (step) {
      case IssueKpUnitsRoutePathsModel['select-units']:
        this.store.dispatch(
          IssueKpUnitsNavigationActions.navigateToSelectUnitTypeAndQuantity()
        );
        break;
      case IssueKpUnitsRoutePathsModel['set-transaction-reference']:
        this.store.dispatch(
          IssueKpUnitsNavigationActions.navigateToSetTransactionReference()
        );
        break;
      case IssueKpUnitsRoutePathsModel['select-commitment-period']:
        this.store.dispatch(
          IssueKpUnitsNavigationActions.navigateToSelectCommitmentPeriod()
        );
    }
  }
}
