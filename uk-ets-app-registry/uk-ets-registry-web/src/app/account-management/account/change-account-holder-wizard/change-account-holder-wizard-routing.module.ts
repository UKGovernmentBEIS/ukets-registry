import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  ChangeAccountHolderSelectionContainerComponent,
  ChangeAccountHolderTypeContainerComponent,
  ChangeAccountHolderOverviewContainerComponent,
  ContactDetailsContainerComponent,
  ContactWorkDetailsContainerComponent,
  RequestSubmittedContainerComponent,
  INDIVIDUAL_ROUTES,
  ORGANISATION_ROUTES,
  CancelContainerComponent,
} from '@change-account-holder-wizard/components';
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    component: ChangeAccountHolderTypeContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPathsModel.BASE_PATH,
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: ChangeAccountHolderWizardPathsModel.ACCOUNT_HOLDER_SELECTION,
    canLoad: [LoginGuard],
    component: ChangeAccountHolderSelectionContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPathsModel.INDIVIDUAL,
    canActivate: [LoginGuard],
    children: INDIVIDUAL_ROUTES,
  },
  {
    path: ChangeAccountHolderWizardPathsModel.ORGANISATION,
    canActivate: [LoginGuard],
    children: ORGANISATION_ROUTES,
  },
  {
    path: ChangeAccountHolderWizardPathsModel.PRIMARY_CONTACT,
    canLoad: [LoginGuard],
    component: ContactDetailsContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPathsModel.PRIMARY_CONTACT_WORK,
    canLoad: [LoginGuard],
    component: ContactWorkDetailsContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPathsModel.CHECK_CHANGE_ACCOUNT_HOLDER,
    canLoad: [LoginGuard],
    //resolve: { goBackPath: CheckUpdateRequestResolver },
    component: ChangeAccountHolderOverviewContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPathsModel.CANCEL_CHANGE_ACCOUNT_HOLDER_REQUEST,
    canLoad: [LoginGuard],
    component: CancelContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPathsModel.REQUEST_SUBMITTED,
    canLoad: [LoginGuard],
    component: RequestSubmittedContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChangeAccountHolderWizardRoutingModule {}
