import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import {
  AcquiringAccountInfo,
  BusinessCheckResult,
  CandidateAcquiringAccounts,
  ReturnExcessAllocationTransactionSummary,
  ReversedAccountInfo,
  SignedReturnExcessAllocationTransactionSummary,
  SignedTransactionSummary,
  TransactionBlockSummaryResult,
  TransactionSummary,
  TransactionType,
  TransactionTypesResult,
} from '@shared/model/transaction';
import {
  TransactionApiService,
  TransactionWizardApiService,
} from '@shared/services';
import { Notice, NoticeStatus } from '@kp-administration/itl-notices/model';

@Injectable()
export class TransactionProposalService {
  constructor(
    private transactionApiService: TransactionApiService,
    private transactionWizardApiService: TransactionWizardApiService
  ) {}

  public getAllowedTransactionTypes(
    accountId: string
  ): Observable<TransactionTypesResult> {
    return this.transactionWizardApiService.getAllowedTransactionTypes(
      accountId
    );
  }

  public getTransactionBlockSummaries(
    accountId: string,
    transactionType: string,
    itlNotificationIdentifier: number
  ): Observable<TransactionBlockSummaryResult> {
    return this.transactionWizardApiService.getTransactionBlockSummaries(
      accountId,
      transactionType,
      itlNotificationIdentifier
    );
  }

  public getTrustedAcquiringAccounts(
    accountIdNumber: number,
    transactionType: string
  ): Observable<CandidateAcquiringAccounts> {
    return this.transactionWizardApiService.getTrustedAcquiringAccounts(
      accountIdNumber,
      transactionType
    );
  }

  public getUserDefinedAcquiringAccount(
    accountIdNumber: number,
    acquiringAccountFullIdentifier: string
  ): Observable<AcquiringAccountInfo> {
    return this.transactionWizardApiService.getUserDefinedAcquiringAccount(
      accountIdNumber,
      acquiringAccountFullIdentifier
    );
  }

  public populateAccountsForReversal(
    transferringAccountFullIdentifier: string,
    acquiringAccountFullIdentifier: string
  ): Observable<ReversedAccountInfo> {
    return this.transactionWizardApiService.populateAccountsForReversal(
      transferringAccountFullIdentifier,
      acquiringAccountFullIdentifier
    );
  }

  public populateAcquiringAccountIdentifier(
    accountIdNumber: number,
    proposedTransactionType: string,
    commitmentPeriod: string,
    itlNotificationIdentifier: number,
    allocationType: string
  ): Observable<AcquiringAccountInfo> {
    return this.transactionWizardApiService.populateAcquiringAccountIdentifier(
      accountIdNumber,
      proposedTransactionType,
      commitmentPeriod,
      itlNotificationIdentifier,
      allocationType
    );
  }

  enrichTransactionSummaryForSigning(
    transactionSummary: TransactionSummary
  ): Observable<TransactionSummary> {
    return this.transactionWizardApiService.enrichTransactionSummaryForSigning(
      transactionSummary
    );
  }

  enrichReturnExcessAllocationTransactionSummaryForSigning(
    transactionSummary: ReturnExcessAllocationTransactionSummary
  ): Observable<ReturnExcessAllocationTransactionSummary> {
    return this.transactionWizardApiService.enrichReturnExcessAllocationTransactionSummaryForSigning(
      transactionSummary
    );
  }

  validate(
    transactionSummary: TransactionSummary,
    businessCheckGroup?: string
  ): Observable<BusinessCheckResult> {
    return this.transactionApiService.validate(
      transactionSummary,
      businessCheckGroup
    );
  }

  validateNatAndNer(
    returnExcessAllocationTransactionSummary: ReturnExcessAllocationTransactionSummary,
    businessCheckGroup?: string
  ): Observable<BusinessCheckResult> {
    return this.transactionApiService.validateNatAndNer(
      returnExcessAllocationTransactionSummary,
      businessCheckGroup
    );
  }

  validateITLNotificationId(
    itlNotificationId: number,
    transactionType: TransactionType
  ): Observable<Notice> {
    return this.transactionApiService.validateITLNotificationId(
      itlNotificationId,
      transactionType
    );
  }

  propose(
    transactionSummary: SignedTransactionSummary
  ): Observable<BusinessCheckResult> {
    return this.transactionApiService.propose(transactionSummary);
  }

  proposeReturnExcessAllocation(
    transactionSummary: SignedReturnExcessAllocationTransactionSummary
  ): Observable<BusinessCheckResult> {
    return this.transactionApiService.proposeReturnExcessAllocation(
      transactionSummary
    );
  }
}
