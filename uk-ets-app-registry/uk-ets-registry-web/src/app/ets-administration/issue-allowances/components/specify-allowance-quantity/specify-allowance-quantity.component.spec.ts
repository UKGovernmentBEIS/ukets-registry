import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { SpecifyAllowanceQuantityComponent } from '@issue-allowances/components';
import { ArraySumPipe } from '@shared/pipes';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { AllowanceQuantityTableComponent } from '@shared/components/transactions/allowance-quantity-table';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-text/uk-proto-form-text.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';

const formBuilder = new FormBuilder();

describe('SpecifyAllowanceQuantityComponent', () => {
  let component: SpecifyAllowanceQuantityComponent;
  let fixture: ComponentFixture<SpecifyAllowanceQuantityComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule],
        declarations: [
          SpecifyAllowanceQuantityComponent,
          ArraySumPipe,
          AllowanceQuantityTableComponent,
          UkProtoFormTextComponent,
          DisableControlDirective,
        ],
        providers: [
          ArraySumPipe,
          { provide: FormBuilder, useValue: formBuilder },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SpecifyAllowanceQuantityComponent);
    component = fixture.componentInstance;
    component.transactionBlockSummaries = [{ quantity: '0' }];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
