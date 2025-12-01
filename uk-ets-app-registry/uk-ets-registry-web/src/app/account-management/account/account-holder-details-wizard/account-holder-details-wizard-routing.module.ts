import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  CheckUpdateRequestResolver,
  UpdateTypesResolver,
} from '@account-management/account/account-holder-details-wizard/resolvers';

import { AccountHolderDetailsWizardPathsModel } from '@account-management/account/account-holder-details-wizard/model';
import {
  CancelUpdateRequestContainerComponent,
  CheckUpdateRequestContainerComponent,
  RequestSubmittedContainerComponent,
  SelectAccountHolderUpdateTypeContainerComponent,
  UpdateAccountHolderDetailsAddressContainerComponent,
  UpdateAccountHolderDetailsContainerComponent,
  UpdateAlternativePrimaryContactDetailsContainerComponent,
  UpdateAlternativePrimaryContactWorkDetailsContainerComponent,
  UpdatePrimaryContactDetailsContainerComponent,
  UpdatePrimaryContactWorkDetailsContainerComponent,
} from '@account-management/account/account-holder-details-wizard/components';
import { UpdateDetailsWarningComponent } from '@shared/components/account/operator/update-details-warning/update-details-warning.component';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    resolve: { updateTypes: UpdateTypesResolver },
    component: SelectAccountHolderUpdateTypeContainerComponent,
  },
  {
    path: AccountHolderDetailsWizardPathsModel.SELECT_UPDATE_TYPE,
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: AccountHolderDetailsWizardPathsModel.UPDATE_ACCOUNT_HOLDER,
    canLoad: [LoginGuard],
    component: UpdateAccountHolderDetailsContainerComponent,
  },
  {
    path: AccountHolderDetailsWizardPathsModel.CONFIRM_UPDATE,
    canLoad: [LoginGuard],
    component: UpdateDetailsWarningComponent,
  },
  {
    path: AccountHolderDetailsWizardPathsModel.UPDATE_AH_ADDRESS,
    canLoad: [LoginGuard],
    component: UpdateAccountHolderDetailsAddressContainerComponent,
  },
  {
    path: AccountHolderDetailsWizardPathsModel.UPDATE_PRIMARY_CONTACT,
    canLoad: [LoginGuard],
    component: UpdatePrimaryContactDetailsContainerComponent,
  },
  {
    path: AccountHolderDetailsWizardPathsModel.UPDATE_PRIMARY_CONTACT_WORK,
    canLoad: [LoginGuard],
    component: UpdatePrimaryContactWorkDetailsContainerComponent,
  },
  {
    path: AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT,
    canLoad: [LoginGuard],
    component: UpdateAlternativePrimaryContactDetailsContainerComponent,
  },
  {
    path: AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT_WORK,
    canLoad: [LoginGuard],
    component: UpdateAlternativePrimaryContactWorkDetailsContainerComponent,
  },
  {
    path: AccountHolderDetailsWizardPathsModel.CHECK_UPDATE_REQUEST,
    canLoad: [LoginGuard],
    resolve: { goBackPath: CheckUpdateRequestResolver },
    component: CheckUpdateRequestContainerComponent,
  },
  {
    path: AccountHolderDetailsWizardPathsModel.CANCEL_UPDATE_REQUEST,
    canLoad: [LoginGuard],
    component: CancelUpdateRequestContainerComponent,
  },
  {
    path: AccountHolderDetailsWizardPathsModel.REQUEST_SUBMITTED,
    canLoad: [LoginGuard],
    component: RequestSubmittedContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountHolderDetailsWizardRoutingModule {}
