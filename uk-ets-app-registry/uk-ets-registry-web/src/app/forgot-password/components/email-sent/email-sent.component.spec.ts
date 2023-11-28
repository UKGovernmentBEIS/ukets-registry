import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';

import { routes } from '../../forgot-password-routing.module';
import { EmailLinkExpiredContainerComponent } from '../email-link-expired/email-link-expired-container.component';
import { EmailLinkExpiredComponent } from '../email-link-expired/email-link-expired.component';
import { ResetPasswordSuccessContainerComponent } from '../reset-password-success/reset-password-success-container.component';
import { ResetPasswordSuccessComponent } from '../reset-password-success/reset-password-success.component';
import { ResetPasswordContainerComponent } from '../reset-password/reset-password-container.component';
import { ResetPasswordComponent } from '../reset-password/reset-password.component';
import { SubmitEmailContainerComponent } from '../submit-email/submit-email-container.component';
import { SubmitEmailComponent } from '../submit-email/submit-email.component';
import { EmailSentContainerComponent } from './email-sent-container.component';

import { EmailSentComponent } from './email-sent.component';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag/govuk-tag.component';
import { UkProtoFormEmailComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-email/uk-proto-form-email.component';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-text/uk-proto-form-text.component';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import {
  PasswordStrengthMeterComponent,
  UkPasswordInputComponent,
} from '@uk-password-control/components';
import { SpinnerComponent } from '@shared/components/spinner';

describe('EmailSentComponent', () => {
  let component: EmailSentComponent;
  let fixture: ComponentFixture<EmailSentComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterTestingModule.withRoutes(routes)],
        declarations: [
          EmailSentComponent,
          EmailSentContainerComponent,
          EmailLinkExpiredComponent,
          EmailLinkExpiredContainerComponent,
          SubmitEmailContainerComponent,
          SubmitEmailComponent,
          ResetPasswordContainerComponent,
          ResetPasswordComponent,
          ResetPasswordSuccessContainerComponent,
          ResetPasswordSuccessComponent,
          BackToTopComponent,
          UkProtoFormEmailComponent,
          UkProtoFormTextComponent,
          UkPasswordInputComponent,
          PasswordStrengthMeterComponent,
          GovukTagComponent,
          DisableControlDirective,
          SpinnerComponent,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailSentComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render title in h2 tag', () => {
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h2').textContent).toContain(
      'Password reset email sent'
    );
  });
});
