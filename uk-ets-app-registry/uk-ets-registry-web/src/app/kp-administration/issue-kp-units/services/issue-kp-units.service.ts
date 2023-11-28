import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  TransactionApiService,
  TransactionWizardApiService
} from '@shared/services';
import {
  AccountInfo,
  BusinessCheckResult,
  CommitmentPeriod,
  RegistryLevelInfoResults,
  RegistryLevelType,
  SignedTransactionSummary,
  TransactionSummary
} from '@shared/model/transaction';

@Injectable({
  providedIn: 'root'
})
export class IssueKpUnitsService {
  constructor(
    private transactionApiService: TransactionApiService,
    private transactionWizardApiService: TransactionWizardApiService
  ) {}

  getAccountsForCommitmentPeriod(
    period: CommitmentPeriod
  ): Observable<AccountInfo[]> {
    return this.transactionWizardApiService.getAccountsForCommitmentPeriod(
      period
    );
  }

  getAccountsForCommitmentPeriodAndType(
    commitmentPeriodCode: CommitmentPeriod,
    registryLevelType: RegistryLevelType
  ): Observable<RegistryLevelInfoResults> {
    return this.transactionWizardApiService.getAccountsForCommitmentPeriodAndType(
      commitmentPeriodCode,
      registryLevelType
    );
  }

  validate(
    transactionSummary: TransactionSummary
  ): Observable<BusinessCheckResult> {
    return this.transactionApiService.validate(transactionSummary);
  }

  enrichTransactionSummaryForSigning(
    transactionSummary: TransactionSummary
  ): Observable<TransactionSummary> {
    return this.transactionWizardApiService.enrichTransactionSummaryForSigning(
      transactionSummary
    );
  }

  proposeIssuance(
    signedTransactionSummary: SignedTransactionSummary
  ): Observable<BusinessCheckResult> {
    return this.transactionApiService.propose(signedTransactionSummary);
  }
}
