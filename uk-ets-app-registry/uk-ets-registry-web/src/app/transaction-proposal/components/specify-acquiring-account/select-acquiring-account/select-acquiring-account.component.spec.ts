import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  SelectAcquiringAccountComponent,
  TitleProposalTransactionTypeComponent,
} from '@transaction-proposal/components';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import {
  ProposedTransactionType,
  TRANSACTION_TYPES_VALUES,
  TransactionType,
} from '@shared/model/transaction';
import {
  OrganisationPipe,
  IndividualPipe,
} from '@shared/pipes/account-holder.pipe';
import {
  Account,
  AccountHolderType,
  AccountType,
  Organisation,
} from '@shared/model/account';
import { ProtectPipe } from '@shared/pipes/protect.pipe';
import { AuthApiService } from '../../../../auth/auth-api.service';
import { MockAuthApiService } from '../../../../../testing/mock-auth-api-service';
import { StoreModule } from '@ngrx/store';
import * as fromAuth from '../../../../auth/auth.reducer';
import { By } from '@angular/platform-browser';
import { AccountInputComponent } from '@shared/form-controls/account-input/account-input.component';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls';

const formBuilder = new FormBuilder();

describe('SpecifyAcquiringAccountComponent', () => {
  let component: SelectAcquiringAccountComponent;
  let fixture: ComponentFixture<SelectAcquiringAccountComponent>;

  const externalTransfer: ProposedTransactionType = {
    type: TransactionType.ExternalTransfer,
    description:
      TRANSACTION_TYPES_VALUES['ExternalTransfer'].label.defaultLabel,
    category: 'KP regular transfers',
    supportsNotification: false,
    skipAccountStep: false,
  };

  const internalTransfer: ProposedTransactionType = {
    type: TransactionType.InternalTransfer,
    description:
      TRANSACTION_TYPES_VALUES['InternalTransfer'].label.defaultLabel,
    category: 'KP regular transfers',
    supportsNotification: false,
    skipAccountStep: false,
  };

  const organisation: Organisation = {
    id: 12345,
    type: AccountHolderType.ORGANISATION,
    details: {
      name: 'Test Name',
      registrationNumber: '1234567',
      noRegistrationNumJustification: '',
    },
    address: {
      buildingAndStreet: '',
      buildingAndStreet2: '',
      buildingAndStreet3: '',
      postCode: '',
      townOrCity: '',
      stateOrProvince: '',
      country: '',
    },
  };

  const transferringAccountPartyHolding: Account = {
    identifier: 1001,
    accountType: AccountType.PARTY_HOLDING_ACCOUNT,
    accountHolder: organisation,
    operator: null,
    authorisedRepresentatives: [],
    accountHolderContactInfo: null,
    // The current rule 2 - Transfers are allowed for accounts outside the TAL.
    trustedAccountListRules: { rule1: true, rule2: false, rule3: true },
    complianceStatus: null,
    balance: null,
    unitType: null,
    accountDetails: {
      accountHolderName: 'BEIS International',
      accountHolderId: '17538',
      complianceStatus: 'NOT_APPLICABLE',
      accountNumber: 'GB-100-1001-0-89',
      name: 'Account Name 01',
      accountType: AccountType.PARTY_HOLDING_ACCOUNT,
      accountStatus: 'OPEN',
      address: null,
      openingDate: null,
      closingDate: null,
      closureReason: null,
      billingContactDetails: null,
      billingEmail1: null,
      billingEmail2: null,
    },
    trustedAccountList: null,
    governmentAccount: true,
    transactionsAllowed: true,
    canBeClosed: false,
    pendingARRequests: null,
    kyotoAccountType: true,
    addedARs: null,
    removedARs: null,
  };

  const transferringAccountPersonHolding: Account = {
    identifier: 11201,
    accountType: AccountType.PERSON_HOLDING_ACCOUNT,
    accountHolder: organisation,
    operator: null,
    authorisedRepresentatives: [],
    accountHolderContactInfo: null,
    // The current rule 2 - Transfers are allowed for accounts outside the TAL.
    trustedAccountListRules: { rule1: true, rule2: true, rule3: true },
    complianceStatus: null,
    balance: null,
    unitType: null,
    accountDetails: {
      accountHolderName: 'Holder same',
      accountHolderId: '17538',
      complianceStatus: 'NOT_APPLICABLE',
      accountNumber: 'GB-121-11201-1-89',
      name: 'Account Name 01',
      accountType: AccountType.PERSON_HOLDING_ACCOUNT,
      accountStatus: 'OPEN',
      address: null,
      openingDate: null,
      closingDate: null,
      closureReason: null,
      billingContactDetails: null,
      billingEmail1: null,
      billingEmail2: null,
    },
    trustedAccountList: null,
    governmentAccount: false,
    transactionsAllowed: true,
    canBeClosed: true,
    pendingARRequests: null,
    kyotoAccountType: true,
    addedARs: null,
    removedARs: null,
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [
          RouterModule.forRoot([]),
          StoreModule.forRoot({ auth: fromAuth.reducer }),
          ReactiveFormsModule,
        ],
        declarations: [
          TitleProposalTransactionTypeComponent,
          AccountInputComponent,
          UkProtoFormTextComponent,
          SelectAcquiringAccountComponent,
          FormGroupDirective,
          IndividualPipe,
          OrganisationPipe,
          ProtectPipe,
        ],
        providers: [
          { provide: FormBuilder, useValue: formBuilder },
          { provide: APP_BASE_HREF, useValue: '/' },
          { provide: AuthApiService, useValue: MockAuthApiService },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectAcquiringAccountComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();

    const headerTitle = fixture.debugElement.nativeElement.querySelector('h1');
    expect(headerTitle).toBeTruthy();
    expect(headerTitle.textContent).toContain('Specify acquiring account');
  });

  it('should display entry form for Kyoto PHAs with allowed outside TAL', () => {
    component.transactionType = externalTransfer;
    component.transferringAccount = transferringAccountPersonHolding;
    component.userDefinedAccountParts = null;
    component.selectedIdentifier = null;
    component.isEtsTransaction = false;
    component.trustedAccountsResult = null;
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.showTrustedAccountOptions()).toBeFalsy();

    const strongTitle =
      fixture.debugElement.nativeElement.querySelector('strong');
    expect(strongTitle).toBeDefined();
    expect(strongTitle.textContent).toContain('Account Number');

    const hintKey = fixture.debugElement.queryAll(By.css('.govuk-hint'))[0];
    expect(hintKey).toBeDefined();
    expect(hintKey.nativeElement.textContent).toContain(
      'For example GB-xxx-xxxxxx-x-xx'
    );
  });

  it('should display trusted list for Kyoto gov accounts under same account holder', () => {
    component.transactionType = internalTransfer;
    component.transferringAccount = transferringAccountPartyHolding;
    component.userDefinedAccountParts = null;
    component.selectedIdentifier = null;
    component.isEtsTransaction = false;
    component.trustedAccountsResult = {
      accountId: 1006,
      trustedAccountsUnderTheSameHolder: [
        {
          trusted: true,
          identifier: 1000,
          fullIdentifier: 'GB-100-1000-1-94',
          accountName: 'Party Holding 1',
          accountHolderName: 'BEIS International',
        },
        {
          trusted: true,
          identifier: 1001,
          fullIdentifier: 'GB-100-1001-1-89',
          accountName: 'Party Holding 2',
          accountHolderName: 'BEIS International',
        },
        {
          trusted: true,
          identifier: 10001078,
          fullIdentifier: 'GB-100-10001078-0-6',
          accountName: 'ESD AAU Deposit Account',
          accountHolderName: 'BEIS International',
        },
      ],
      otherTrustedAccounts: [],
      predefinedCandidateAccounts: [],
      candidateListPredefined: false,
    };
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.showTrustedAccountOptions()).toBeTruthy();
    const headerKey = fixture.debugElement.queryAll(
      By.css('.govuk-table__header')
    )[0];
    expect(headerKey).toBeDefined();
    expect(headerKey.nativeElement.textContent).toContain('Account number');

    const accountFullIdentifierKey1 = fixture.debugElement.queryAll(
      By.css('.govuk-radios__label')
    )[0];
    expect(accountFullIdentifierKey1).toBeDefined();
    expect(accountFullIdentifierKey1.nativeElement.textContent).toContain(
      'GB-100-1000-1-94'
    );

    const cellKey1 = fixture.debugElement.queryAll(
      By.css('.govuk-table__cell')
    )[0];
    expect(cellKey1).toBeDefined();
    expect(cellKey1.nativeElement.textContent).toContain('Party Holding 1');

    const accountFullIdentifierKey2 = fixture.debugElement.queryAll(
      By.css('.govuk-radios__label')
    )[1];
    expect(accountFullIdentifierKey2).toBeDefined();
    expect(accountFullIdentifierKey2.nativeElement.textContent).toContain(
      'GB-100-1001-1-89'
    );

    const cellKey2 = fixture.debugElement.queryAll(
      By.css('.govuk-table__cell')
    )[1];
    expect(cellKey2).toBeDefined();
    expect(cellKey2.nativeElement.textContent).toContain('Party Holding 2');
  });
});
