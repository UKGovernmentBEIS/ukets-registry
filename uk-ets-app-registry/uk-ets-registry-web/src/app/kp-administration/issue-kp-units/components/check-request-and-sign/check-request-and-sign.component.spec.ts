import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckRequestAndSignComponent } from './check-request-and-sign.component';
import { RemainingQuantityPipe } from '../../pipes/remaining-quantity.pipe';
import {
  IssuanceTransactionSummaryTableComponent,
  TransactionSigningDetailsComponent,
} from '@shared/components/transactions';
import { UnitTypeAndActivityPipe } from '../../pipes/registry-level-info-view.pipe';
import { GdsDateShortPipe } from '@shared/pipes';
import { SignRequestFormComponent } from '@signing/components';

import { ReactiveFormsModule } from '@angular/forms';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-text/uk-proto-form-text.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import { RouterTestingModule } from '@angular/router/testing';
import {
  TransactionReferenceComponent,
  TransactionReferenceWarningComponent,
} from '@shared/components/transactions/transaction-reference';

describe('CheckRequestAndSignComponent', () => {
  let component: CheckRequestAndSignComponent;
  let fixture: ComponentFixture<CheckRequestAndSignComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterTestingModule],
        declarations: [
          CheckRequestAndSignComponent,
          RemainingQuantityPipe,
          IssuanceTransactionSummaryTableComponent,
          UnitTypeAndActivityPipe,
          SignRequestFormComponent,
          TransactionSigningDetailsComponent,
          GdsDateShortPipe,
          UkProtoFormTextComponent,
          DisableControlDirective,
          TransactionReferenceComponent,
          TransactionReferenceWarningComponent,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckRequestAndSignComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
