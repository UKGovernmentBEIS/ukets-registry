import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SubmitEmailContainerComponent } from './components/submit-email/submit-email-container.component';
import { EmailSentContainerComponent } from './components/email-sent/email-sent-container.component';
import { ResetPasswordContainerComponent } from './components/reset-password/reset-password-container.component';
import { EmailLinkExpiredContainerComponent } from './components/email-link-expired/email-link-expired-container.component';
import { ResetPasswordSuccessContainerComponent } from './components/reset-password-success/reset-password-success-container.component';

export const routes: Routes = [
  {
    path: '',
    component: SubmitEmailContainerComponent
  },
  {
    path: 'email-sent',
    component: EmailSentContainerComponent
  },
  {
    path: 'email-link-expired',
    component: EmailLinkExpiredContainerComponent
  },
  {
    path: 'reset-password/:token',
    component: ResetPasswordContainerComponent
  },
  {
    path: 'reset-password-success',
    component: ResetPasswordSuccessContainerComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ForgotPasswordRoutingModule {}
