import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionProposalSubmittedComponent } from '@transaction-proposal/components';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { AccountType } from '@shared/model/account';
import {
  ConcatDateTimePipe,
  GdsDateShortPipe,
  GdsTimePipe,
} from '@shared/pipes';
import { By } from '@angular/platform-browser';

describe('TransactionProposalSubmittedComponent', () => {
  let component: TransactionProposalSubmittedComponent;
  let fixture: ComponentFixture<TransactionProposalSubmittedComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          TransactionProposalSubmittedComponent,
          RouterLinkDirectiveStub,
          ConcatDateTimePipe,
          GdsTimePipe,
          GdsDateShortPipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionProposalSubmittedComponent);
    component = fixture.componentInstance;
    component.account = {
      identifier: 1,
      accountType: AccountType.UK_ALLOCATION_ACCOUNT,
      accountHolder: null,
      accountHolderContactInfo: null,
      accountDetails: null,
      operator: null,
      authorisedRepresentatives: [],
      trustedAccountListRules: null,
      complianceStatus: '',
      balance: 0,
      unitType: '',
      governmentAccount: true,
      trustedAccountList: null,
      transactionsAllowed: false,
      canBeClosed: false,
      pendingARRequests: [],
      kyotoAccountType: null,
      addedARs: null,
      removedARs: null,
    };
    component.enrichedReturnExcessAllocationTransactionSummaryForSigning = {
      allocationYear: 0,
      blocks: [],
      comment: '',
      natAcquiringAccountInfo: undefined,
      natQuantity: 0,
      natReturnTransactionIdentifier: undefined,
      nerAcquiringAccountInfo: undefined,
      nerQuantity: 0,
      nerReturnTransactionIdentifier: undefined,
      returnExcessAllocationType: undefined,
      transferringAccountIdentifier: 0,
      type: undefined,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render transaction identifier', () => {
    component.businessCheckResult = {
      requestIdentifier: null,
      transactionIdentifier: 'UK88383',
      approvalRequired: null,
      executionDate: null,
      executionTime: null,
      transactionTypeDescription: null,
    };
    component.enrichedReturnExcessAllocationTransactionSummaryForSigning =
      undefined;
    fixture.detectChanges();
    const body = fixture.debugElement.queryAll(By.css('.govuk-panel__body'));
    expect(body[0].nativeElement.textContent).toContain(
      'The transaction ID is UK88383'
    );
  });

  it('should render multiple transaction identifiers', () => {
    component.businessCheckResult = {
      requestIdentifier: null,
      transactionIdentifier: 'UK88383(NAT),UK7756(NER)',
      approvalRequired: null,
      executionDate: null,
      executionTime: null,
      transactionTypeDescription: null,
    };

    component.enrichedReturnExcessAllocationTransactionSummaryForSigning = {
      allocationYear: 0,
      blocks: [],
      comment: '',
      natAcquiringAccountInfo: undefined,
      natQuantity: 0,
      natReturnTransactionIdentifier: 'UK88383',
      nerAcquiringAccountInfo: undefined,
      nerQuantity: 0,
      nerReturnTransactionIdentifier: 'UK7756',
      returnExcessAllocationType: undefined,
      transferringAccountIdentifier: 0,
      type: undefined,
    };
    fixture.detectChanges();
    const body = fixture.debugElement.queryAll(By.css('.govuk-panel__body'));
    expect(
      body[0].nativeElement.textContent.toString().trim().replace(/\s+/g, ' ')
    ).toContain('The transaction IDs are UK7756 (NER) and UK88383 (NAT)');
  });
});
