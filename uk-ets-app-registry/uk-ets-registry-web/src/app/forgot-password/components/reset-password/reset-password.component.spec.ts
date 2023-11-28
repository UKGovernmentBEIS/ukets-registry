import { APP_BASE_HREF } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  PWNED_PASSWORDS_API_URL,
  UK_ETS_PASSWORD_VALIDATION_API_BASE_URL,
} from '@registry-web/app.tokens';
import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';
import { ResetPasswordComponent } from './reset-password.component';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag/govuk-tag.component';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-text/uk-proto-form-text.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import {
  PasswordStrengthMeterComponent,
  UkPasswordInputComponent,
} from '@uk-password-control/components';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { selectPasswordStrengthScore } from '@uk-password-control/store/reducers';
import { PasswordBlacklistAsyncValidator } from '@shared/validation/password-blacklist-async-validator';
import { PasswordStrengthAsyncValidator } from '@shared/validation/password-strength-async-validator';
import { SpinnerComponent } from '@shared/components/spinner';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;
  let mockStore: MockStore;
  let mockConfigurationSelector: any;
  const initialStateConfiguration = [
    {
      'password.policy.minimum-chars': 10,
    },
  ];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          ResetPasswordComponent,
          UkProtoFormTextComponent,
          BackToTopComponent,
          PasswordStrengthMeterComponent,
          UkPasswordInputComponent,
          GovukTagComponent,
          DisableControlDirective,
          SpinnerComponent,
        ],
        imports: [
          ReactiveFormsModule,
          HttpClientTestingModule,
          RouterModule.forRoot([]),
        ],
        providers: [
          provideMockStore({
            selectors: [{ selector: selectPasswordStrengthScore, value: 0 }],
          }),
          PasswordBlacklistAsyncValidator,
          PasswordStrengthAsyncValidator,
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
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetPasswordComponent);
    mockStore = TestBed.inject(MockStore);
    mockConfigurationSelector = mockStore.overrideSelector(
      'configuration',
      initialStateConfiguration
    );
    component = fixture.componentInstance;
    component.configuration = initialStateConfiguration;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render password labels correctly', () => {
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('#passwordsGroup-label').textContent).toMatch(
      'New password:'
    );
    expect(compiled.querySelector('#pconfirmLabel').textContent).toMatch(
      'Confirm new password:'
    );
  });
});
