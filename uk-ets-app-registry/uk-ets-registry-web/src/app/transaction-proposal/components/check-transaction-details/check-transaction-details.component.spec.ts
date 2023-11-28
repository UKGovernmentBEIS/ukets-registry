import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  CheckTransactionDetailsComponent,
  TitleProposalTransactionTypeComponent,
  TransactionCommentFormComponent,
} from '@transaction-proposal/components';
import { AccountSummaryComponent } from '@shared/components/transactions/account-summary/account-summary.component';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { QuantityTableComponent } from '@shared/components/transactions/quantity-table';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import {
  EnvironmentalActivityPipe,
  GdsDateShortPipe,
  TransactionAtrributesPipe,
  TrustedAccountPipe,
  UnitTypeSopRenderPipe,
} from '@shared/pipes';
import { TransactionType } from '@shared/model/transaction';
import {
  ItlNotificationSummaryComponent,
  TransactionSigningDetailsComponent,
} from '@shared/components/transactions';
import { SignRequestFormComponent } from '@signing/components';
import { ReactiveFormsModule } from '@angular/forms';
import {
  UkProtoFormCommentAreaComponent,
  UkProtoFormTextComponent,
} from '@shared/form-controls/uk-proto-form-controls';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import { YearOfReturnComponent } from '@transaction-proposal/components/year-of-return/year-of-return.component';
import {
  TransactionReferenceComponent,
  TransactionReferenceWarningComponent,
} from '@shared/components/transactions/transaction-reference';

describe('CheckTransactionDetailsComponent', () => {
  let component: CheckTransactionDetailsComponent;
  let fixture: ComponentFixture<CheckTransactionDetailsComponent>;
  let store: MockStore;
  const initialState = { loggedIn: false };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterModule.forRoot([])],
        declarations: [
          CheckTransactionDetailsComponent,
          AccountSummaryComponent,
          RouterLinkDirectiveStub,
          QuantityTableComponent,
          EnvironmentalActivityPipe,
          TrustedAccountPipe,
          UnitTypeSopRenderPipe,
          TransactionSigningDetailsComponent,
          GdsDateShortPipe,
          UkProtoFormCommentAreaComponent,
          UkProtoFormTextComponent,
          TransactionCommentFormComponent,
          ItlNotificationSummaryComponent,
          SignRequestFormComponent,
          TitleProposalTransactionTypeComponent,
          DisableControlDirective,
          TransactionAtrributesPipe,
          YearOfReturnComponent,
          TransactionReferenceComponent,
          TransactionReferenceWarningComponent,
        ],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          provideMockStore({ initialState }),
          // other providers
        ],
      }).compileComponents();

      store = TestBed.inject(MockStore);
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckTransactionDetailsComponent);
    component = fixture.componentInstance;
    component.itlNotification = null;
    component.transactionSummary = {
      ...component.transactionSummary,
      reversedIdentifier: 'UK54321',
      identifier: 'UK12345',
    };
    component.proposedTransactionType = {
      type: TransactionType.InternalTransfer,
      description: '',
      category: '',
      supportsNotification: false,
      skipAccountStep: false,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    store.setState({ loggedIn: true });
    expect(component).toBeTruthy();
  });
});
