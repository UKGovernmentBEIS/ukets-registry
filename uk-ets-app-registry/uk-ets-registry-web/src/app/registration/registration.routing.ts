import { RouterModule, Routes } from '@angular/router';
import { EmailVerifyComponent } from './email-verify/email-verify.component';
import { CheckAnswersAndSubmitComponent } from './check-answers-and-submit/check-answers-and-submit.component';
import { RegisteredComponent } from './registered/registered.component';
import { EmailConfirmComponent } from './email-confirm/email-confirm.component';
import { PersonalDetailsContainerComponent } from './personal-details/personal-details-container.component';
import { WorkDetailsContainerComponent } from './work-details/work-details-container.component';
import { NgModule } from '@angular/core';
import { EmailAddressContainerComponent } from './email-address/email-address-container.component';
import { ChoosePasswordContainerComponent } from './choose-password/choose-password.container.component';
import { EmailInfoContainerComponent } from '@registry-web/registration/email-info/email-info-container.component';
import { MemorablePhraseContainerComponent } from '@registry-web/registration/memorable-phrase';

export const routes: Routes = [
  { path: 'emailInfo', component: EmailInfoContainerComponent },
  { path: 'emailAddress', component: EmailAddressContainerComponent },
  { path: 'emailVerify', component: EmailVerifyComponent },
  { path: 'emailVerify/:submittedEmail', component: EmailVerifyComponent },
  {
    path: 'emailConfirm/:token',
    component: EmailConfirmComponent
  },
  {
    path: 'personal-details',
    component: PersonalDetailsContainerComponent
  },
  {
    path: 'work-details',
    component: WorkDetailsContainerComponent
  },
  {
    path: 'memorable-phrase',
    component: MemorablePhraseContainerComponent
  },
  { path: 'choose-password', component: ChoosePasswordContainerComponent },
  {
    path: 'check-answers-and-submit',
    component: CheckAnswersAndSubmitComponent
  },
  { path: 'registered', component: RegisteredComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RegistrationRoutingModule {}
