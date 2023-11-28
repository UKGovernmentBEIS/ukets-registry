import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../app.tokens';
import {
  AccountDetails,
  AccountHolder,
  AccountType,
  AuthorisedRepresentative,
  Operator,
  TrustedAccountListRules,
} from '@shared/model/account';
import {
  AccountHolderContact,
  AccountHolderContactInfo,
} from '@shared/model/account/account-holder-contact';

export class RequestRepresentation {
  requestId: number;
  type: string;
  status: string;
}

/**
 * TODO: To be replaced with the AccountDTO;
 */

export class AccountRepresentation {
  accountType: AccountType;
  accountHolder: AccountHolder;
  transferringAccountHolder: AccountHolder;
  accountHolderContacts: AccountHolderContact[];
  accountHolderContactInfo: AccountHolderContactInfo;
  transferringAccountHolderContactInfo: AccountHolderContactInfo;
  accountDetails: AccountDetails;
  trustedAccountListRules: TrustedAccountListRules;
  operator: Operator;
  authorisedRepresentatives: AuthorisedRepresentative[];
  //TODO: change this to oldAccountHolderId to align with keeping the old account holder id state
  changedAccountHolderId: number;
  installationToBeTransferred: Operator;
  accountDetailsSameBillingAddress?: boolean;
}

// TODO  UKETS-4527 This is a duplicate of the AccountRepresentation class that will ultimate replace it
export interface AccountDTO {
  accountType: AccountType;
  accountHolder: AccountHolder;
  accountHolderContacts: AccountHolderContact[];
  accountHolderContactInfo: AccountHolderContactInfo;
  accountDetails: AccountDetails;
  trustedAccountListRules: TrustedAccountListRules;
  operator: Operator;
  authorisedRepresentatives: AuthorisedRepresentative[];
  changedAccountHolderId: number;
}

@Injectable({
  providedIn: 'root',
})
export class AccountOpeningService {
  jsonContent = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  createAccount(account: AccountRepresentation) {
    return this.httpClient.post<RequestRepresentation>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.propose`,
      account,
      this.jsonContent
    );
  }
}
