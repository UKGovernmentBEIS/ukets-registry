import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  AccountInfo,
  AcquiringAccountInfo,
  CandidateAcquiringAccounts,
  CommitmentPeriod,
  RegistryLevelInfoResults,
  RegistryLevelType,
  ReturnExcessAllocationTransactionSummary,
  ReversedAccountInfo,
  TransactionBlockSummaryResult,
  TransactionSummary,
  TransactionType,
  TransactionTypesResult,
} from '@shared/model/transaction';
import { Observable } from 'rxjs';

/**
 * api calls to the transaction wizard controller
 */
@Injectable({ providedIn: 'root' })
export class TransactionWizardApiService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  getAccountsForCommitmentPeriod(
    period: CommitmentPeriod
  ): Observable<AccountInfo[]> {
    return this.httpClient.get<AccountInfo[]>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.issuance-accounts`,
      {
        params: {
          commitmentPeriodCode: period,
        },
      }
    );
  }

  getAccountsForCommitmentPeriodAndType(
    commitmentPeriodCode: CommitmentPeriod,
    registryLevelType: RegistryLevelType
  ): Observable<RegistryLevelInfoResults> {
    return this.httpClient.get<RegistryLevelInfoResults>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.registry-levels`,
      {
        params: {
          commitmentPeriodCode,
          registryLevelType,
        },
      }
    );
  }

  enrichTransactionSummaryForSigning(
    transactionSummary: TransactionSummary
  ): Observable<TransactionSummary> {
    return this.httpClient.post<TransactionSummary>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.complete-transaction-summary`,
      transactionSummary
    );
  }

  enrichReturnExcessAllocationTransactionSummaryForSigning(
    transactionSummary: ReturnExcessAllocationTransactionSummary
  ): Observable<ReturnExcessAllocationTransactionSummary> {
    return this.httpClient.post<ReturnExcessAllocationTransactionSummary>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.excess-allocation-nat-and-ner.complete-transaction-summary`,
      transactionSummary
    );
  }

  public getTransactionBlockSummaries(
    accountId: string,
    transactionType: string,
    itlNotificationIdentifier: number
  ): Observable<TransactionBlockSummaryResult> {
    const params = {
      accountId,
      transactionType,
    };
    if (itlNotificationIdentifier) {
      params['itlNotificationIdentifier'] = itlNotificationIdentifier;
    }
    return this.httpClient.get<TransactionBlockSummaryResult>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.transaction-block-summaries`,
      {
        params,
      }
    );
  }

  public getAllowanceBlockSummaries(): Observable<TransactionBlockSummaryResult> {
    return this.httpClient.get<TransactionBlockSummaryResult>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.allowance-block-summaries`,
      {
        params: {
          transactionType: TransactionType.IssueAllowances,
        },
      }
    );
  }

  public populateAllowanceIdentifier(): Observable<AcquiringAccountInfo> {
    return this.httpClient.get<AcquiringAccountInfo>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.allowances-acquiring-account`,
      {
        params: {
          proposedTransactionType: TransactionType.IssueAllowances,
        },
      }
    );
  }

  public populateAcquiringAccountIdentifier(
    accountIdNumber: number,
    proposedTransactionType: string,
    commitmentPeriod: string,
    itlNotificationIdentifier: number,
    allocationType: string
  ): Observable<AcquiringAccountInfo> {
    const accountId = accountIdNumber.toString();
    const params = {
      accountId,
      proposedTransactionType,
      commitmentPeriod,
    };
    if (itlNotificationIdentifier) {
      params['itlNotificationIdentifier'] = itlNotificationIdentifier;
    }
    if (allocationType) {
      params['allocationType'] = allocationType;
    }
    return this.httpClient.get<AcquiringAccountInfo>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.acquiring-account-details`,
      {
        params,
      }
    );
  }

  public getUserDefinedAcquiringAccount(
    accountIdNumber: number,
    acquiringAccountFullIdentifier: string
  ): Observable<AcquiringAccountInfo> {
    const accountId = accountIdNumber.toString();
    return this.httpClient.get<AcquiringAccountInfo>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.user-defined-acquiring-account`,
      {
        params: {
          accountId,
          acquiringAccountFullIdentifier,
        },
      }
    );
  }

  public populateAccountsForReversal(
    transferringAccountFullIdentifier: string,
    acquiringAccountFullIdentifier: string
  ): Observable<ReversedAccountInfo> {
    return this.httpClient.get<ReversedAccountInfo>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.reversed-accounts`,
      {
        params: {
          transferringAccountFullIdentifier,
          acquiringAccountFullIdentifier,
        },
      }
    );
  }

  public getTrustedAcquiringAccounts(
    accountIdNumber: number,
    transactionType: string
  ): Observable<CandidateAcquiringAccounts> {
    const accountId = accountIdNumber.toString();
    let params;
    if (transactionType === TransactionType.CentralTransferAllowances) {
      params = new HttpParams()
        .set('accountId', accountId)
        .set('transactionType', transactionType);
    } else {
      params = new HttpParams().set('accountId', accountId);
    }
    return this.httpClient.get<CandidateAcquiringAccounts>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.candidate-acquiring-accounts`,
      {
        params,
      }
    );
  }

  public getAllowedTransactionTypes(
    accountId: string
  ): Observable<TransactionTypesResult> {
    let params = new HttpParams();
    params = params.set('accountId', accountId);
    return this.httpClient.get<TransactionTypesResult>(
      `${this.ukEtsRegistryApiBaseUrl}/transaction-wizard.get.transaction-types`,
      {
        params,
      }
    );
  }
}
