import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { StoreModule } from '@ngrx/store';
import * as registrationTestHelper from 'src/app/registration/registration.test.helper';
import { Router, ActivatedRoute } from '@angular/router';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { ConnectFormDirective } from 'src/app/shared/connect-form.directive';
import { ChoosePasswordComponent } from './choose-password.component';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { PasswordBlacklistAsyncValidator } from '@shared/validation/password-blacklist-async-validator';
import { PasswordStrengthAsyncValidator } from '@shared/validation/password-strength-async-validator';
import {
  PWNED_PASSWORDS_API_URL,
  UK_ETS_PASSWORD_VALIDATION_API_BASE_URL,
} from '@registry-web/app.tokens';
import { APP_BASE_HREF } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SpinnerComponent } from '@shared/components/spinner';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));
const formBuilder = new FormBuilder();

describe('ChoosePasswordComponent', () => {
  let fixture: ComponentFixture<ChoosePasswordComponent>;
  let component: ChoosePasswordComponent;
  const initialState = {
    registration: registrationTestHelper.aTestRegistrationState,
    shared: registrationTestHelper.aTestRegistrationState,
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          ConnectFormDirective,
          FormGroupDirective,
          ChoosePasswordComponent,
          ScreenReaderPageAnnounceDirective,
          SpinnerComponent,
        ],
        providers: [
          { provide: Router, useValue: routerSpy },
          PasswordBlacklistAsyncValidator,
          PasswordStrengthAsyncValidator,
          { provide: ActivatedRoute, useValue: activatedRouteSpy },
          { provide: FormBuilder, useValue: formBuilder },
          { provide: APP_BASE_HREF, useValue: '/' },
          {
            provide: PWNED_PASSWORDS_API_URL,
            useValue: 'pwnedPasswordsApiUrl',
          },
          {
            provide: UK_ETS_PASSWORD_VALIDATION_API_BASE_URL,
            useValue: 'ukEtsPasswordValidationApiBaseUrl',
          },
        ],
        imports: [
          StoreModule.forRoot(initialState),
          HttpClientTestingModule,
          ReactiveFormsModule,
        ],
      }).compileComponents();
      fixture = TestBed.createComponent(ChoosePasswordComponent);
      component = fixture.debugElement.componentInstance;
    })
  );

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });
});
