import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  TitleProposalTransactionTypeComponent,
  TransactionCommentFormComponent,
} from '@transaction-proposal/components';
import { AccountSummaryComponent } from '@shared/components/transactions/account-summary/account-summary.component';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { QuantityTableComponent } from '@shared/components/transactions/quantity-table';
import {
  EnvironmentalActivityPipe,
  GdsDateShortPipe,
  TrustedAccountPipe,
  UnitTypeSopRenderPipe,
} from '@shared/pipes';
import { TransactionType } from '@shared/model/transaction';
import { TransactionSigningDetailsComponent } from '@shared/components/transactions';
import { SignRequestFormComponent } from '@signing/components';
import { ReactiveFormsModule } from '@angular/forms';
import {
  UkProtoFormCommentAreaComponent,
  UkProtoFormTextComponent,
} from '@shared/form-controls/uk-proto-form-controls';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';

describe('TitleProposalTransactionTypeComponent', () => {
  let component: TitleProposalTransactionTypeComponent;
  let fixture: ComponentFixture<TitleProposalTransactionTypeComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterModule.forRoot([])],
        declarations: [
          TitleProposalTransactionTypeComponent,
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
          SignRequestFormComponent,
          DisableControlDirective,
        ],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TitleProposalTransactionTypeComponent);
    component = fixture.componentInstance;
    component.proposalTransactionType = {
      type: TransactionType.InternalTransfer,
      description: '',
      category: '',
      supportsNotification: false,
      skipAccountStep: false,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
