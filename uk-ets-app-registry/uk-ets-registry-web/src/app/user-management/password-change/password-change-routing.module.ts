import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { NgModule } from '@angular/core';
import { NewPasswordFormContainerComponent } from '@password-change/component/new-password-form';
import { ChangePasswordConfirmationContainerComponent } from '@password-change/component/confirmation-page';
import { PasswordChangeRoutePaths } from '@password-change/model';

export const routes: Routes = [
  {
    path: PasswordChangeRoutePaths.CHANGE_PASSWORD_PATH,
    canActivate: [LoginGuard],
    component: NewPasswordFormContainerComponent
  },
  {
    path:
      PasswordChangeRoutePaths.CHANGE_PASSWORD_CONFIRMATION_PATH + '/:email',
    component: ChangePasswordConfirmationContainerComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PasswordChangeRoutingModule {}
