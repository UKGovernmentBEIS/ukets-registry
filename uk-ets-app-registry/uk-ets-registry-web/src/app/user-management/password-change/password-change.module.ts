import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { PasswordChangeRoutingModule } from '@password-change/password-change-routing.module';
import { NewPasswordFormContainerComponent } from '@password-change/component/new-password-form';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { UkPasswordControlModule } from '@registry-web/uk-password-control/uk-password-control.module';
import { ChangePasswordConfirmationContainerComponent } from '@password-change/component/confirmation-page';
import { NewPasswordFormComponent } from '@password-change/component/new-password-form/new-password-form.component';
import { PasswordChangeNavigationEffects } from '@password-change/effect';
import * as fromChangePassword from '@password-change/reducer/password-change.reducer';

@NgModule({
  declarations: [
    NewPasswordFormContainerComponent,
    NewPasswordFormComponent,
    ChangePasswordConfirmationContainerComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    UkPasswordControlModule,
    PasswordChangeRoutingModule,
    StoreModule.forFeature(
      fromChangePassword.passwordChangeFeatureKey,
      fromChangePassword.reducer
    ),
    EffectsModule.forFeature([PasswordChangeNavigationEffects])
  ],
  providers: []
})
export class PasswordChangeModule {}
