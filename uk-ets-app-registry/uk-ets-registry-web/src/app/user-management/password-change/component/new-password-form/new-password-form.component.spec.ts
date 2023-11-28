import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NewPasswordFormComponent } from './new-password-form.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  PWNED_PASSWORDS_API_URL,
  UK_ETS_PASSWORD_VALIDATION_API_BASE_URL,
} from '@registry-web/app.tokens';
import { APP_BASE_HREF } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  PasswordStrengthMeterComponent,
  UkPasswordInputComponent,
} from '@uk-password-control/components';
import { PasswordStrengthAsyncValidator } from '@shared/validation/password-strength-async-validator';
import { PasswordBlacklistAsyncValidator } from '@shared/validation/password-blacklist-async-validator';
import { SpinnerComponent } from '@shared/components/spinner';

describe('NewPasswordFormComponent', () => {
  let component: NewPasswordFormComponent;
  let fixture: ComponentFixture<NewPasswordFormComponent>;
  let mockStore: MockStore;
  let mockConfigurationSelector: any;
  const initialStateConfiguration = [
    {
      'password.policy.minimum-chars': 10,
    },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        HttpClientTestingModule,
      ],
      declarations: [
        UkPasswordInputComponent,
        NewPasswordFormComponent,
        PasswordStrengthMeterComponent,
        SpinnerComponent,
      ],
      providers: [
        provideMockStore(),
        PasswordBlacklistAsyncValidator,
        PasswordStrengthAsyncValidator,
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: PWNED_PASSWORDS_API_URL, useValue: 'pwnedPasswordsApiUrl' },
        {
          provide: UK_ETS_PASSWORD_VALIDATION_API_BASE_URL,
          useValue: 'ukEtsPasswordValidationApiBaseUrl',
        },
      ],
    }).compileComponents();
    mockStore = TestBed.inject(MockStore);
    mockConfigurationSelector = mockStore.overrideSelector(
      'configuration',
      initialStateConfiguration
    );
    fixture = TestBed.createComponent(NewPasswordFormComponent);
    fixture.componentInstance.configuration = initialStateConfiguration;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewPasswordFormComponent);
    component = fixture.debugElement.componentInstance;
    component.configuration = initialStateConfiguration;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
