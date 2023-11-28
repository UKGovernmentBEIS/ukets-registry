import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  SpecifyUnitTypesAndQuantityComponent,
  TitleProposalTransactionTypeComponent,
} from '@transaction-proposal/components';
import {
  FormBuilder,
  FormGroupDirective,
  FormGroupName,
  ReactiveFormsModule,
} from '@angular/forms';
import { UnitTypeSopRenderPipe } from '@shared/pipes/unit-type-sop-render.pipe';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import {
  ProposedTransactionType,
  TransactionType,
} from '@shared/model/transaction';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ItlNotificationSummaryComponent } from '@shared/components/transactions';

const formBuilder = new FormBuilder();

describe('SelectUnitTypesAndQuantityComponent', () => {
  let component: SpecifyUnitTypesAndQuantityComponent;
  let fixture: ComponentFixture<SpecifyUnitTypesAndQuantityComponent>;

  const transactionType: ProposedTransactionType = {
    type: TransactionType.ExternalTransfer,
    description: 'Transfer KP units',
    category: 'KP regular transfers',
    supportsNotification: false,
    skipAccountStep: false,
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterModule.forRoot([]), ReactiveFormsModule],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          TitleProposalTransactionTypeComponent,
          SpecifyUnitTypesAndQuantityComponent,
          ItlNotificationSummaryComponent,
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
    fixture = TestBed.createComponent(SpecifyUnitTypesAndQuantityComponent);
    component = fixture.componentInstance;
    component.selectedTransactionBlockSummaries = [];
    component.transactionType = transactionType;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
