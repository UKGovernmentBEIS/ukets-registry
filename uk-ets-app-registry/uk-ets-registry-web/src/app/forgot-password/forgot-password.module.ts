import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ForgotPasswordRoutingModule } from './forgot-password-routing.module';
import { SubmitEmailComponent } from './components/submit-email/submit-email.component';
import { SubmitEmailContainerComponent } from './components/submit-email/submit-email-container.component';
import { EmailSentComponent } from './components/email-sent/email-sent.component';
import { EmailSentContainerComponent } from './components/email-sent/email-sent-container.component';
import { EmailLinkExpiredComponent } from './components/email-link-expired/email-link-expired.component';
import { EmailLinkExpiredContainerComponent } from './components/email-link-expired/email-link-expired-container.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { ResetPasswordContainerComponent } from './components/reset-password/reset-password-container.component';
import { ResetPasswordSuccessComponent } from './components/reset-password-success/reset-password-success.component';
import { ResetPasswordSuccessContainerComponent } from './components/reset-password-success/reset-password-success-container.component';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { ForgotPasswordEffects } from './store/effects';
import { StoreModule } from '@ngrx/store';
import * as fromForgotPasswordReducer from './store/reducers';
import { UkPasswordControlModule } from '../uk-password-control/uk-password-control.module';

@NgModule({
  declarations: [
    SubmitEmailComponent,
    SubmitEmailContainerComponent,
    EmailSentComponent,
    EmailSentContainerComponent,
    EmailLinkExpiredComponent,
    EmailLinkExpiredContainerComponent,
    ResetPasswordComponent,
    ResetPasswordContainerComponent,
    ResetPasswordSuccessComponent,
    ResetPasswordSuccessContainerComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    UkPasswordControlModule,
    ReactiveFormsModule,
    ForgotPasswordRoutingModule,
    StoreModule.forFeature(
      fromForgotPasswordReducer.forgotPasswordFeatureKey,
      fromForgotPasswordReducer.reducer
    ),
    EffectsModule.forFeature([ForgotPasswordEffects])
  ]
})
export class ForgotPasswordModule {}
