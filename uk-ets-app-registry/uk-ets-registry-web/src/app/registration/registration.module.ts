import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { EmailConfirmComponent } from './email-confirm/email-confirm.component';
import { EmailVerifyComponent } from './email-verify/email-verify.component';
import { EmailInfoComponent } from './email-info/email-info.component';
import { EmailAddressComponent } from './email-address/email-address.component';
import { SharedModule } from '@shared/shared.module';
import { ChoosePasswordComponent } from './choose-password/choose-password.component';
import { CheckAnswersAndSubmitComponent } from './check-answers-and-submit/check-answers-and-submit.component';
import { RegisteredComponent } from './registered/registered.component';

import * as fromRegistration from './registration.reducer';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { RegistrationEffects } from './registration.effect';
import { PersonalDetailsContainerComponent } from './personal-details/personal-details-container.component';
import { WorkDetailsContainerComponent } from './work-details/work-details-container.component';
import { RegistrationRoutingModule } from './registration.routing';
import { EmailAddressContainerComponent } from './email-address/email-address-container.component';
import { EmailVerifyNotReceivedEmailComponent } from './email-verify/email-verify-not-received-email.component';
import { UkPasswordControlModule } from '@registry-web/uk-password-control/uk-password-control.module';
import { ChoosePasswordContainerComponent } from './choose-password/choose-password.container.component';
import { EmailInfoContainerComponent } from '@registry-web/registration/email-info/email-info-container.component';
import { MemorablePhraseContainerComponent } from '@registry-web/registration/memorable-phrase';

@NgModule({
  declarations: [
    EmailConfirmComponent,
    EmailVerifyComponent,
    EmailInfoComponent,
    EmailInfoContainerComponent,
    EmailAddressComponent,
    EmailAddressContainerComponent,
    PersonalDetailsContainerComponent,
    ChoosePasswordComponent,
    ChoosePasswordContainerComponent,
    WorkDetailsContainerComponent,
    MemorablePhraseContainerComponent,
    CheckAnswersAndSubmitComponent,
    RegisteredComponent,
    EmailVerifyNotReceivedEmailComponent,
  ],
  imports: [
    CommonModule,
    RegistrationRoutingModule,
    SharedModule,
    ReactiveFormsModule,
    UkPasswordControlModule,
    StoreModule.forFeature(
      fromRegistration.registrationFeatureKey,
      fromRegistration.reducer
    ),
    EffectsModule.forFeature([RegistrationEffects]),
  ],
  exports: [],
})
export class RegistrationModule {}
