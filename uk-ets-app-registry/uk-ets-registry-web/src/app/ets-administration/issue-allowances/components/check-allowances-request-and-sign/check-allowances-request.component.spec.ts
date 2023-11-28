import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckAllowancesRequestComponent } from '@issue-allowances/components';
import { AllowanceQuantityTableComponent } from '@shared/components/transactions/allowance-quantity-table';

import { ReactiveFormsModule } from '@angular/forms';
import { ArraySumPipe, GdsDateShortPipe } from '@shared/pipes';
import { TransactionSigningDetailsComponent } from '@shared/components/transactions';
import { SignRequestFormComponent } from '@signing/components';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-text/uk-proto-form-text.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import {
  TransactionReferenceComponent,
  TransactionReferenceWarningComponent,
} from '@shared/components/transactions/transaction-reference';

describe('CheckAllowancesRequestComponent', () => {
  let component: CheckAllowancesRequestComponent;
  let fixture: ComponentFixture<CheckAllowancesRequestComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule],
        declarations: [
          CheckAllowancesRequestComponent,
          AllowanceQuantityTableComponent,
          SignRequestFormComponent,
          UkProtoFormTextComponent,
          ArraySumPipe,
          TransactionSigningDetailsComponent,
          GdsDateShortPipe,
          DisableControlDirective,
          TransactionReferenceComponent,
          TransactionReferenceWarningComponent,
        ],
        providers: [ArraySumPipe],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckAllowancesRequestComponent);
    component = fixture.componentInstance;
    component.transactionBlockSummaries = [{ quantity: '0' }];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
