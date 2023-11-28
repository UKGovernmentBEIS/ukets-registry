import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { PhoneInfo, UKSelectPhoneComponent } from '@shared/form-controls';
import { SharedModule } from '@registry-web/shared/shared.module';

describe('UKSelectPhoneComponent', () => {
  let testHost: TestHostComponent;
  let fixture: ComponentFixture<TestHostComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestHostComponent],
      imports: [ReactiveFormsModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestHostComponent);
    testHost = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be invalid at first', () => {
    expect(testHost).toBeTruthy();
    expect(testHost.ukSelectPhoneGroup.get('phone').valid).toBeFalsy();
  });

  it('should be valid when inserting a valid uk phone number', () => {
    const validPhoneNumber: PhoneInfo = {
      countryCode: 'UK (44)',
      phoneNumber: '7798222333',
    };
    testHost.ukSelectPhoneGroup.setValue({ phone: validPhoneNumber });
    expect(testHost.ukSelectPhoneGroup.valid).toBeTruthy();
    expect(testHost.ukSelectPhoneGroup.get('phone').errors).toBeNull();
  });

  it('should be invalid with invalidChars when inserting a phone number containing letters', () => {
    const phoneNumberWithLetters: PhoneInfo = {
      countryCode: 'UK (44)',
      phoneNumber: '7798ff222334',
    };
    testHost.ukSelectPhoneGroup.setValue({ phone: phoneNumberWithLetters });
    expect(testHost.ukSelectPhoneGroup.invalid).toBeTruthy();
    const errors = testHost.ukSelectPhoneGroup.get('phone').errors;
    expect(errors).toStrictEqual({
      invalidChars: 'The phone number contains invalid characters',
    });
  });

  it('should be invalid with invalidChars when inserting a phone number containing symbols', () => {
    const phoneNumberWithLetters: PhoneInfo = {
      countryCode: 'UK (44)',
      phoneNumber: '7798@@222333',
    };
    testHost.ukSelectPhoneGroup.setValue({ phone: phoneNumberWithLetters });
    expect(testHost.ukSelectPhoneGroup.invalid).toBeTruthy();
    const errors = testHost.ukSelectPhoneGroup.get('phone').errors;
    expect(errors).toStrictEqual({
      invalidChars: 'The phone number contains invalid characters',
    });
  });

  it('should be invalid with invalidCodeForCountry when phone number is uk and country selected is gr', () => {
    const ukPhoneNumberForGr: PhoneInfo = {
      countryCode: 'GR (30)',
      phoneNumber: '7798222333',
    };
    testHost.ukSelectPhoneGroup.setValue({ phone: ukPhoneNumberForGr });
    expect(testHost.ukSelectPhoneGroup.invalid).toBeTruthy();
    const errors = testHost.ukSelectPhoneGroup.get('phone').errors;
    expect(errors).toStrictEqual({
      invalidCodeForCountry:
        'Please enter a valid phone format for the selected country',
    });
  });

  it('should be invalid with tooShort when phone number is too short', () => {
    const tooShortNumber: PhoneInfo = {
      countryCode: 'GR (30)',
      phoneNumber: '210',
    };
    testHost.ukSelectPhoneGroup.setValue({ phone: tooShortNumber });
    expect(testHost.ukSelectPhoneGroup.invalid).toBeTruthy();
    const errors = testHost.ukSelectPhoneGroup.get('phone').errors;
    expect(errors).toStrictEqual({
      tooShort: 'The phone number is too short for your country code',
    });
  });

  it('should be invalid with tooLong when phone number is too long', () => {
    const tooLongNumber: PhoneInfo = {
      countryCode: 'UK (44)',
      phoneNumber: '77982223337',
    };
    testHost.ukSelectPhoneGroup.setValue({ phone: tooLongNumber });
    expect(testHost.ukSelectPhoneGroup.invalid).toBeTruthy();
    const errors = testHost.ukSelectPhoneGroup.get('phone').errors;
    expect(errors).toStrictEqual({
      tooLong: 'The phone number is too long for your country code',
    });
  });
});

@Component({
  selector: 'app-test-host-component',
  template: `
    <form [formGroup]="ukSelectPhoneGroup">
      <app-uk-phone-select
        [showErrors]="true"
        formControlName="phone"
      ></app-uk-phone-select>
      <button class="govuk-button" data-module="govuk-button" type="submit">
        Continue
      </button>
    </form>
  `,
})
class TestHostComponent {
  ukSelectPhoneGroup = new FormGroup(
    {
      phone: new FormControl(''),
    },
    { updateOn: 'submit' }
  );
}
