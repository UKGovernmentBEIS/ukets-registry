import { HttpClientTestingModule } from '@angular/common/http/testing';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import {
  PWNED_PASSWORDS_API_URL,
  UK_ETS_PASSWORD_VALIDATION_API_BASE_URL,
} from '@registry-web/app.tokens';

import { GovukTagComponent } from '@shared/govuk-components/govuk-tag/govuk-tag.component';
import { PasswordStrengthValidatorService } from '@uk-password-control/validation';
import {
  PasswordStrengthMeterComponent,
  UkPasswordInputComponent,
} from '@uk-password-control/components';
import { provideMockStore } from '@ngrx/store/testing';

describe('UkPasswordInputComponent', () => {
  let component: UkPasswordInputComponent;
  let fixture: ComponentFixture<UkPasswordInputComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          UkPasswordInputComponent,
          PasswordStrengthMeterComponent,
          GovukTagComponent,
        ],
        imports: [ReactiveFormsModule, HttpClientTestingModule],
        providers: [
          provideMockStore(),
          PasswordStrengthValidatorService,
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
    fixture = TestBed.createComponent(UkPasswordInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
