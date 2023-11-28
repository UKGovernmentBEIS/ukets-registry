import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectAccountStatusActionComponent } from './select-account-status-action.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { APP_BASE_HREF } from '@angular/common';
import { AccountStatusActionOption } from '@shared/model/account';
import { By } from '@angular/platform-browser';

describe('SelectAccountStatusActionComponent', () => {
  let component: SelectAccountStatusActionComponent;
  let fixture: ComponentFixture<SelectAccountStatusActionComponent>;

  const accountStatusActions: AccountStatusActionOption[] = [
    {
      label: 'Unblock account',
      hint: 'Allow unrestricted transactions for the account.',
      value: 'REMOVE_RESTRICTIONS',
      enabled: true,
      newStatus: 'OPEN',
    },
  ];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          SelectAccountStatusActionComponent,
          UkRadioInputComponent,
          RouterLinkDirectiveStub,
        ],
        imports: [ReactiveFormsModule, RouterModule.forRoot([])],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectAccountStatusActionComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render title in h1 tag', () => {
    component.allowedAccountStatusActions = accountStatusActions;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain('Select Action');
  });

  test('should have as caption `Change the account status`', () => {
    component.allowedAccountStatusActions = accountStatusActions;
    fixture.detectChanges();
    const caption = fixture.debugElement.query(By.css('.govuk-caption-xl'));
    expect(caption.nativeElement.textContent).toContain(
      'Change the account status'
    );
  });

  test('the account status action types are rendered correctly', () => {
    component.allowedAccountStatusActions = accountStatusActions;
    fixture.detectChanges();

    accountStatusActions.forEach((action, index) => {
      const labelName = fixture.debugElement.nativeElement.querySelector(
        `label[for=accountStatusAction${index}]`
      ).textContent;
      expect(action.label).toBe(labelName.trim());
    });
  });

  test('selection of account status action is required', () => {
    component.allowedAccountStatusActions = accountStatusActions;
    fixture.detectChanges();

    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup)
        .accountStatusAction
    ).toBe('You must select an action');

    // error message
    const errorMessage = fixture.debugElement.query(
      By.css('.govuk-error-message')
    ).nativeElement.textContent;
    expect(errorMessage.trim()).toBe('Error:You must select an action');
  });

  test('account status action is set correctly', () => {
    component.allowedAccountStatusActions = accountStatusActions;
    fixture.detectChanges();

    accountStatusActions.forEach((type, index) => {
      const firstSelection = fixture.debugElement.query(
        By.css(`#accountStatusAction${index}`)
      );

      firstSelection.triggerEventHandler('change', {
        target: firstSelection.nativeElement,
      });

      const continueButton = fixture.debugElement.query(By.css('button'));
      continueButton.nativeElement.click();
      fixture.detectChanges();

      const accountStatusAction = fixture.componentInstance.formGroup.get(
        'accountStatusAction'
      ).value;
      expect(component.getAllFormGroupValidationErrors()).toBeNull();
      expect(accountStatusAction).toBe(accountStatusActions[index].value);
    });
  });
});
