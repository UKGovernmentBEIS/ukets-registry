import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { SelectTypeUserDetailsUpdateContainerComponent } from '@user-update/component/select-type-user-details-update';
import { UserDetailsUpdateWizardPathsModel } from '@user-update/model';
import { UpdateUserPersonalDetailsContainerComponent } from '@user-update/component/update-user-personal-details';
import { CancelUpdateRequestContainerComponent } from '@user-update/component/cancel-update-request';
import { UpdateUserWorkDetailsContainerComponent } from '@user-update/component/update-user-work-details';
import { CheckUserDetailsUpdateRequestContainerComponent } from '@user-update/component/check-user-details-update-request';
import { SelectUserDetailsInfoGuard } from '@user-update/guards';
import { RequestSubmittedContainerComponent } from '@user-update/component/request-submitted';
import { UserDeactivationCommentContainerComponent } from '@user-update/component/user-deactivation-comment';
import { CheckDeactivationDetailsContainerComponent } from '@user-update/component/check-deactivation-details';
import { UpdateUserMemorablePhraseContainerComponent } from '@user-update/component/update-user-memorable-phrase';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    children: [
      {
        path: '',
        canActivate: [SelectUserDetailsInfoGuard],
        component: SelectTypeUserDetailsUpdateContainerComponent,
      },
    ],
  },
  {
    path: UserDetailsUpdateWizardPathsModel.PERSONAL_DETAILS,
    canLoad: [LoginGuard],
    component: UpdateUserPersonalDetailsContainerComponent,
  },
  {
    path: UserDetailsUpdateWizardPathsModel.WORK_DETAILS,
    canLoad: [LoginGuard],
    component: UpdateUserWorkDetailsContainerComponent,
  },
  {
    path: UserDetailsUpdateWizardPathsModel.MEMORABLE_PHRASE,
    canLoad: [LoginGuard],
    component: UpdateUserMemorablePhraseContainerComponent,
  },
  {
    path: UserDetailsUpdateWizardPathsModel.DEACTIVATION_COMMENT,
    canLoad: [LoginGuard],
    component: UserDeactivationCommentContainerComponent,
  },
  {
    path: UserDetailsUpdateWizardPathsModel.CANCEL_UPDATE_REQUEST,
    canLoad: [LoginGuard],
    component: CancelUpdateRequestContainerComponent,
  },
  {
    path: UserDetailsUpdateWizardPathsModel.CHECK_USER_DETAILS_UPDATE_REQUEST,
    canLoad: [LoginGuard],
    component: CheckUserDetailsUpdateRequestContainerComponent,
  },
  {
    path: UserDetailsUpdateWizardPathsModel.CHECK_DEACTIVATION_DETAILS_REQUEST,
    canLoad: [LoginGuard],
    component: CheckDeactivationDetailsContainerComponent,
  },
  {
    path: UserDetailsUpdateWizardPathsModel.REQUEST_SUBMITTED,
    canLoad: [LoginGuard],
    component: RequestSubmittedContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserDetailsUpdateWizardRoutingModule {}
