import {
  SpecifyQuantityComponent,
  TitleProposalTransactionTypeComponent,
} from '@transaction-proposal/components';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import {
  FormBuilder,
  FormGroupDirective,
  FormGroupName,
  ReactiveFormsModule,
} from '@angular/forms';
import { RouterModule } from '@angular/router';

import { APP_BASE_HREF, CommonModule } from '@angular/common';
import { UnitTypeSopRenderPipe } from '@shared/pipes';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  ProposedTransactionType,
  TransactionType,
} from '@shared/model/transaction';
import { ItlNotificationSummaryComponent } from '@shared/components/transactions';

const formBuilder = new FormBuilder();

describe('SpecifyQuantityComponent', () => {
  let component: SpecifyQuantityComponent;
  let fixture: ComponentFixture<SpecifyQuantityComponent>;

  const transactionType: ProposedTransactionType = {
    type: TransactionType.CentralTransferAllowances,
    description: 'Transfer KP units',
    category: 'KP regular transfers',
    supportsNotification: false,
    skipAccountStep: false,
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [CommonModule, RouterModule.forRoot([]), ReactiveFormsModule],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          TitleProposalTransactionTypeComponent,
          ItlNotificationSummaryComponent,
          SpecifyQuantityComponent,
          FormGroupDirective,
          FormGroupName,
          UnitTypeSopRenderPipe,
        ],
        providers: [
          UnitTypeSopRenderPipe,
          { provide: FormBuilder, useValue: formBuilder },
          { provide: APP_BASE_HREF, useValue: '/' },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SpecifyQuantityComponent);
    component = fixture.componentInstance;
    component.transactionBlockSummaries = [];
    component.selectedTransactionBlockSummaries = [];
    component.transactionType = transactionType;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
