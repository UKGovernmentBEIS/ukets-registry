import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { NgModule } from '@angular/core';
import {
  EmailChangeVerificationContainerComponent,
  NewEmailFormContainerComponent,
  EmailChangeConfirmationContainerComponent
} from '@email-change/component';
import { EmailChangeRoutePath } from '@email-change/model';
import { EmailChangeConfirmationResolver } from '@email-change/resolver/email-change-confirmation.resolver';

export const routes: Routes = [
  {
    path: EmailChangeRoutePath.ENTER_NEW_EMAIL_PATH,
    canActivate: [LoginGuard],
    component: NewEmailFormContainerComponent
  },
  {
    path: EmailChangeRoutePath.EMAIL_CHANGE_REQUEST_VERIFICATION_PATH,
    canActivate: [LoginGuard],
    component: EmailChangeVerificationContainerComponent
  },
  {
    path: EmailChangeRoutePath.EMAIL_CHANGE_CONFIRMATION_PATH,
    resolve: { confirmation: EmailChangeConfirmationResolver },
    component: EmailChangeConfirmationContainerComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EmailChangeRoutingModule {}
