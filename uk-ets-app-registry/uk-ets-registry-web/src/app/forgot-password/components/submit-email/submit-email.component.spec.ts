import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { SubmitEmailComponent } from './submit-email.component';
import { APP_BASE_HREF } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';
import { By } from '@angular/platform-browser';
import { UkProtoFormEmailComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-email/uk-proto-form-email.component';

describe('SubmitEmailComponent', () => {
  let component: SubmitEmailComponent;
  let fixture: ComponentFixture<SubmitEmailComponent>;
  const linkExpiration = 60;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          SubmitEmailComponent,
          UkProtoFormEmailComponent,
          BackToTopComponent,
        ],
        imports: [ReactiveFormsModule, RouterModule.forRoot([])],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SubmitEmailComponent);
    component = fixture.componentInstance;
    component.cookiesAccepted = true;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render title in h2 tag', () => {
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain(
      'Reset my password'
    );
  });

  test('should render specific text in p tag', () => {
    component.linkExpiration = linkExpiration;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('p').textContent.trim()).toContain(
      `We'll send you a link to reset your password. The link will expire after ` +
        linkExpiration +
        ' minutes.'
    );
  });

  test('email is required', () => {
    // component.emailControl.value = '';
    fixture.detectChanges();

    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup).email
    ).toBe('Email address is required.');

    // error message
    const errorMessage = fixture.debugElement.query(
      By.css('.govuk-error-message')
    ).nativeElement.textContent;
    expect(errorMessage.trim()).toBe('Error: Email address is required.');
  });

  test('email is required', () => {
    fixture.detectChanges();
    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    expect(
      component.genericValidator.processMessages(component.formGroup).email
    ).toBe('Email address is required.');

    // error message
    const errorMessage = fixture.debugElement.query(
      By.css('.govuk-error-message')
    ).nativeElement.textContent;
    expect(errorMessage.trim()).toBe('Error: Email address is required.');
  });

  test('email is set correctly', () => {
    const emailInput = fixture.debugElement.query(By.css('input'));
    emailInput.nativeElement.value = 'invalid_email';
    emailInput.nativeElement.dispatchEvent(new Event('input'));

    fixture.detectChanges();
    const email = fixture.componentInstance.formGroup.get('email').value;
    expect(email).toBe(emailInput.nativeElement.value);
  });

  // test('email must be in the correct format', () => {
  //   const emailInput = fixture.debugElement.query(By.css('input'));
  //   emailInput.nativeElement.value = 'invalid_email';
  //   emailInput.nativeElement.dispatchEvent(new Event('input'));

  //   fixture.detectChanges();
  //   const continueButton = fixture.debugElement.query(By.css('button'));
  //   continueButton.nativeElement.click();
  //   fixture.detectChanges();

  //   expect(component.getAllFormGroupValidationErrors()).toBeDefined();
  //   expect(component.genericValidator.processMessages(component.formGroup).email).
  //   toBe('Enter an email address in the correct format, like name@example.com');

  //   // error message
  //   const errorMessage = fixture.debugElement.query(
  //     By.css('.govuk-error-message')
  //   ).nativeElement.textContent;
  //   expect(errorMessage.trim()).toBe(
  //     'Error: Enter an email address in the correct format, like name@example.com'
  //   );
  // });
});
