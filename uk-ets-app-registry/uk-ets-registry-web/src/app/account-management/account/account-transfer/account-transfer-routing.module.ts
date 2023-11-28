import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { SelectAccountTransferAcquiringAccountHolderContainerComponent } from '@account-transfer/components/select-account-transfer-acquiring-account-holder';
import { CancelAccountTransferContainerComponent } from '@account-transfer/components/cancel-account-transfer';
import { AccountTransferPathsModel } from '@account-transfer/model';
import { CheckAccountTransferContainerComponent } from '@account-transfer/components/check-account-transfer';
import { SubmittedAccountTransferContainerComponent } from '@account-transfer/components/submitted-account-transfer';
import { AcquiringOrganisationDetailsContainerComponent } from '@account-transfer/components/acquiring-organisation-details/acquiring-organisation-details-container.component';
import { AcquiringOrganisationDetailsAddressContainerComponent } from '@account-transfer/components/acquiring-organisation-details-address';
import { AcquiringPrimaryContactDetailsContainerComponent } from '@account-transfer/components/acquiring-primary-contact-details';
import { AcquiringPrimaryContactWorkDetailsContainerComponent } from '@account-transfer/components/acquiring-primary-contact-work-details';
import { CheckAccountTransferRequestResolver } from '@account-transfer/resolvers';

export const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    component: SelectAccountTransferAcquiringAccountHolderContainerComponent,
  },
  {
    path: AccountTransferPathsModel.SELECT_UPDATE_TYPE,
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: AccountTransferPathsModel.UPDATE_ACCOUNT_HOLDER,
    canLoad: [LoginGuard],
    component: AcquiringOrganisationDetailsContainerComponent,
  },
  {
    path: AccountTransferPathsModel.UPDATE_AH_ADDRESS,
    canLoad: [LoginGuard],
    component: AcquiringOrganisationDetailsAddressContainerComponent,
  },
  {
    path: AccountTransferPathsModel.UPDATE_PRIMARY_CONTACT,
    canLoad: [LoginGuard],
    component: AcquiringPrimaryContactDetailsContainerComponent,
  },
  {
    path: AccountTransferPathsModel.UPDATE_PRIMARY_CONTACT_WORK,
    canLoad: [LoginGuard],
    component: AcquiringPrimaryContactWorkDetailsContainerComponent,
  },
  {
    path: AccountTransferPathsModel.CANCEL_ACCOUNT_TRANSFER_REQUEST,
    canLoad: [LoginGuard],
    component: CancelAccountTransferContainerComponent,
  },
  {
    path: AccountTransferPathsModel.CHECK_ACCOUNT_TRANSFER,
    canLoad: [LoginGuard],
    resolve: { goBackPath: CheckAccountTransferRequestResolver },
    component: CheckAccountTransferContainerComponent,
  },
  {
    path: AccountTransferPathsModel.REQUEST_SUBMITTED,
    canLoad: [LoginGuard],
    component: SubmittedAccountTransferContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountTransferRoutingModule {}
