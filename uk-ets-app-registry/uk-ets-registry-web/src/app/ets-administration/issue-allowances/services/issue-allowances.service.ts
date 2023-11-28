import { Injectable } from '@angular/core';
import {
  AcquiringAccountInfo,
  BusinessCheckResult,
  SignedTransactionSummary,
  TransactionBlockSummaryResult,
  TransactionSummary
} from '@shared/model/transaction';
import { Observable } from 'rxjs';
import {
  TransactionApiService,
  TransactionWizardApiService
} from '@shared/services';

@Injectable({
  providedIn: 'root'
})
export class IssueAllowancesService {
  constructor(
    private transactionApiService: TransactionApiService,
    private transactionWizardApiService: TransactionWizardApiService
  ) {}

  public getAllowanceBlockSummaries(): Observable<
    TransactionBlockSummaryResult
  > {
    return this.transactionWizardApiService.getAllowanceBlockSummaries();
  }

  public populateAllowanceIdentifier(): Observable<AcquiringAccountInfo> {
    return this.transactionWizardApiService.populateAllowanceIdentifier();
  }

  propose(
    signedTransactionSummary: SignedTransactionSummary
  ): Observable<BusinessCheckResult> {
    return this.transactionApiService.proposeAllowance(
      signedTransactionSummary
    );
  }

  enrichTransactionSummaryForSigning(
    transactionSummary: TransactionSummary
  ): Observable<TransactionSummary> {
    return this.transactionWizardApiService.enrichTransactionSummaryForSigning(
      transactionSummary
    );
  }
}
