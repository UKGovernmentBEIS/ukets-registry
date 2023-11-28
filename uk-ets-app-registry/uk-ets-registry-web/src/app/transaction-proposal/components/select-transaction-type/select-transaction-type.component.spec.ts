import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SelectTransactionTypeComponent } from '@transaction-proposal/components';
import {
  ProposedTransactionType,
  TransactionType,
} from '@shared/model/transaction';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { By } from '@angular/platform-browser';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';

describe('SelectTransactionTypeComponent', () => {
  let component: SelectTransactionTypeComponent;
  let fixture: ComponentFixture<SelectTransactionTypeComponent>;

  const transactionTypes: ProposedTransactionType[] = [
    {
      type: TransactionType.ExternalTransfer,
      description: 'Transfer KP units',
      category: 'KP regular transfers',
      supportsNotification: false,
      skipAccountStep: false,
    },
    {
      type: TransactionType.TransferToSOPforFirstExtTransferAAU,
      description: 'Transfer to SOP for First External Transfer of AAU',
      category: 'KP other',
      supportsNotification: false,
      skipAccountStep: false,
    },
  ];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          SelectTransactionTypeComponent,
          UkRadioInputComponent,
          RouterLinkDirectiveStub,
        ],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectTransactionTypeComponent);
    component = fixture.componentInstance;
  });

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });

  test('the transaction types are rendered correctly', () => {
    component.allowedTransactionTypes = {
      accountId: '123456',
      result: transactionTypes,
    };
    fixture.detectChanges();

    transactionTypes.forEach((type, index) => {
      const labelName = fixture.debugElement.nativeElement.querySelector(
        `label[for=transactionType${index}]`
      ).textContent;
      expect(type.description).toBe(labelName.trim());
    });
  });

  test('no options in case of null transaction type list', () => {
    component.allowedTransactionTypes = null;
    fixture.detectChanges();

    const pageContent = fixture.debugElement.query(By.css('.govuk-grid-row'));
    expect(pageContent).toBeNull();
    expect(component.getAllowedTransactionTypes()).toBeUndefined();
  });

  test('selection of transaction type is required', () => {
    component.allowedTransactionTypes = {
      accountId: '123456',
      result: transactionTypes,
    };
    fixture.detectChanges();

    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    const errorMessage = fixture.debugElement.query(
      By.css('.govuk-error-message')
    ).nativeElement.textContent;
    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup)
        .transactionType
    ).toBe('Select a transaction type');
    expect(errorMessage.trim()).toBe('Error:Select a transaction type');
  });

  test('transaction type is set correctly', () => {
    component.allowedTransactionTypes = {
      accountId: '123456',
      result: transactionTypes,
    };
    fixture.detectChanges();

    transactionTypes.forEach((type, index) => {
      const firstSelection = fixture.debugElement.query(
        By.css(`#transactionType${index}`)
      );

      firstSelection.triggerEventHandler('change', {
        target: firstSelection.nativeElement,
      });

      const continueButton = fixture.debugElement.query(By.css('button'));
      continueButton.nativeElement.click();
      fixture.detectChanges();

      const transactionType = fixture.componentInstance.formGroup.get(
        'transactionType'
      ).value;
      expect(component.getAllFormGroupValidationErrors()).toBeNull();
      expect(transactionType).toBe(transactionTypes[index]);
    });
  });
});
