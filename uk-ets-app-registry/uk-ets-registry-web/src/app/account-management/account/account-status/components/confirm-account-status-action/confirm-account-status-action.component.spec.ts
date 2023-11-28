import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmAccountStatusActionComponent } from './confirm-account-status-action.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { By } from '@angular/platform-browser';
import { UkProtoFormCommentAreaComponent } from '@shared/form-controls/uk-proto-form-controls';

describe('ConfirmAccountStatusActionComponent', () => {
  let component: ConfirmAccountStatusActionComponent;
  let fixture: ComponentFixture<ConfirmAccountStatusActionComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          ConfirmAccountStatusActionComponent,
          UkProtoFormCommentAreaComponent,
          RouterLinkDirectiveStub,
        ],
        imports: [ReactiveFormsModule, RouterModule.forRoot([])],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmAccountStatusActionComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render title in h1 tag', () => {
    component.currentAccountStatus = 'OPEN';
    component.accountStatusAction = {
      label: 'Suspend account (full)',
      hint:
        'Authorised Representatives will not be able to access or view the account. The account will not be able to receive units.',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain(
      'Check your update and confirm'
    );
  });

  test('should render warning text in govuk-warning-text__text', () => {
    component.currentAccountStatus = 'OPEN';
    component.accountStatusAction = {
      label: 'Block account',
      hint:
        'This will not restrict Surrender, Reverse Allocation or Return Excess Allocation transactions for the account.',
      value: 'RESTRICT_SOME_TRANSACTIONS',
      newStatus: 'SOME_TRANSACTIONS_RESTRICTED',
      message:
        'You are restricting some transactions of an account that already has all of its transactions restricted',
    };
    fixture.detectChanges();
    const warningText = fixture.debugElement.query(
      By.css('.govuk-warning-text__text')
    );
    expect(warningText.nativeElement.textContent).toContain(
      'You are restricting some transactions of an account that already has all of its transactions restricted'
    );
  });

  test('should have as caption `Change the account status`', () => {
    component.currentAccountStatus = 'OPEN';
    component.accountStatusAction = {
      label: 'Suspend account (full)',
      hint:
        'Authorised Representatives will not be able to access or view the account. The account will not be able to receive units.',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();
    const caption = fixture.debugElement.query(By.css('.govuk-caption-xl'));
    expect(caption.nativeElement.textContent).toContain(
      'Change the account status'
    );
  });

  test('the current account status is rendered correctly', () => {
    component.currentAccountStatus = 'OPEN';
    component.accountStatusAction = {
      label: 'Suspent account (full)',
      hint:
        'Authorised Representatives will not be able to access or view the account. The account will not be able to receive units.',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[0];
    expect(key.nativeElement.textContent).toContain('Current account status');
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[0];
    expect(value.nativeElement.textContent).toContain('Open');
  });

  test('the account status action is rendered correctly', () => {
    component.currentAccountStatus = 'OPEN';
    component.accountStatusAction = {
      label: 'Suspent account (full)',
      hint:
        'Authorised Representatives will not be able to access or view the account. The account will not be able to receive units.',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[1];
    expect(key.nativeElement.textContent).toContain('Action');
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[1];
    expect(value.nativeElement.textContent).toContain('Suspent account (full)');
  });

  test('the new account status is rendered correctly', () => {
    component.currentAccountStatus = 'OPEN';
    component.accountStatusAction = {
      label: 'Suspent account (full)',
      hint:
        'Authorised Representatives will not be able to access or view the account. The account will not be able to receive units.',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[2];
    expect(key.nativeElement.textContent).toContain('New account status');
    const value = fixture.debugElement.queryAll(
      By.css('.panel-change-notification')
    )[0];
    expect(value.nativeElement.textContent).toContain('Suspended');
  });

  test('the comment textarea is rendered correctly', () => {
    component.currentAccountStatus = 'OPEN';
    component.accountStatusAction = {
      label: 'Suspent account (full)',
      hint:
        'Authorised Representatives will not be able to access or view the account. The account will not be able to receive units.',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();
    const key = fixture.debugElement.query(By.css('.govuk-label'));
    expect(key.nativeElement.textContent).toContain('Enter some comments');
    const value = fixture.debugElement.queryAll(By.css('textarea'));
    expect(value).toBeTruthy();
  });

  test('comment is required', () => {
    component.currentAccountStatus = 'OPEN';
    component.accountStatusAction = {
      label: 'Suspent account (full)',
      hint:
        'Authorised Representatives will not be able to access or view the account. The account will not be able to receive units.',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();

    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup).comment
    ).toBe('You must enter a comment');

    //error message
    const errorMessage = fixture.debugElement.query(
      By.css('.govuk-error-message')
    ).nativeElement.textContent;
    expect(errorMessage.trim()).toBe('Error: You must enter a comment');
  });

  test('comment is set correctly', () => {
    component.currentAccountStatus = 'OPEN';
    component.accountStatusAction = {
      label: 'Suspent account (full)',
      hint:
        'Authorised Representatives will not be able to access or view the account. The account will not be able to receive units.',
      value: 'SUSPEND',
      newStatus: 'SUSPENDED',
    };
    fixture.detectChanges();

    const textarea = fixture.debugElement.query(By.css('textarea'));
    textarea.nativeElement.value =
      'Account is no longer eligible for transfers';
    textarea.nativeElement.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    const comment = fixture.componentInstance.formGroup.get('comment').value;

    expect(comment).toBe(textarea.nativeElement.value);
  });
});
