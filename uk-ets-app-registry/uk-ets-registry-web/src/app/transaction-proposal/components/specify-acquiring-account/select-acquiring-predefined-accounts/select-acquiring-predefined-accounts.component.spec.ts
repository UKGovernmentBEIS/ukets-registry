import {
  SelectAcquiringPredefinedAccountsComponent,
  TitleProposalTransactionTypeComponent,
} from '@transaction-proposal/components';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import {
  CandidateAcquiringAccounts,
  ProposedTransactionType,
  TRANSACTION_TYPES_VALUES,
  TransactionType,
} from '@shared/model/transaction';
import { APP_BASE_HREF, CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  FormBuilder,
  FormGroupDirective,
  FormGroupName,
  ReactiveFormsModule,
} from '@angular/forms';

const formBuilder = new FormBuilder();

describe('SelectAcquiringPredefinedAccountsComponent', () => {
  let component: SelectAcquiringPredefinedAccountsComponent;
  let fixture: ComponentFixture<SelectAcquiringPredefinedAccountsComponent>;

  const transactionType: ProposedTransactionType = {
    type: TransactionType.ExternalTransfer,
    description:
      TRANSACTION_TYPES_VALUES['ExternalTransfer'].label.defaultLabel,
    category: 'KP regular transfers',
    supportsNotification: false,
    skipAccountStep: false,
  };

  const result: CandidateAcquiringAccounts = {
    accountId: 1001,
    trustedAccountsUnderTheSameHolder: [],
    otherTrustedAccounts: [],
    predefinedCandidateAccounts: [],
    candidateListPredefined: true,
    predefinedCandidateAccountsDescription: 'test',
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CommonModule, RouterModule.forRoot([]), ReactiveFormsModule],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          TitleProposalTransactionTypeComponent,
          SelectAcquiringPredefinedAccountsComponent,
          FormGroupDirective,
          FormGroupName,
        ],
        providers: [
          { provide: FormBuilder, useValue: formBuilder },
          { provide: APP_BASE_HREF, useValue: '/' },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(
      SelectAcquiringPredefinedAccountsComponent
    );
    component = fixture.componentInstance;
    component.candidateAcquiringAccountsResult = result;
    component.transactionType = transactionType;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
