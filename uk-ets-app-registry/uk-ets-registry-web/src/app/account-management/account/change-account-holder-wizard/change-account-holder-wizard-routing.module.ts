import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@registry-web/shared/guards';
import {
  ChangeAccountHolderSelectionContainerComponent,
  ChangeAccountHolderTypeContainerComponent,
  ChangeAccountHolderOverviewContainerComponent,
  ContactDetailsContainerComponent,
  ContactWorkDetailsContainerComponent,
  RequestSubmittedContainerComponent,
  CancelContainerComponent,
  IndividualDetailsContainerComponent,
  IndividualContactDetailsContainerComponent,
  OrganisationDetailsContainerComponent,
  OrganisationAddressContainerComponent,
  DeleteOrphanAccountHolderContainerComponent,
} from '@change-account-holder-wizard/components';
import {
  canActivateChangeAccountHolderAddNewStep,
  canActivateSelectExistingOrAddNewStep,
  canActivateChangeAccountHolderDeleteOrphan,
  canActivateChangeAccountHolderOverview,
} from '@registry-web/account-management/account/change-account-holder-wizard/guards-and-resolvers';
import { ChangeAccountHolderWizardPaths } from '@change-account-holder-wizard/model';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: ChangeAccountHolderTypeContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.SELECT_TYPE,
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: ChangeAccountHolderWizardPaths.SELECT_EXISTING_OR_ADD_NEW,
    canActivate: [LoginGuard, canActivateSelectExistingOrAddNewStep],
    component: ChangeAccountHolderSelectionContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.INDIVIDUAL_DETAILS,
    canActivate: [LoginGuard, canActivateChangeAccountHolderAddNewStep],
    component: IndividualDetailsContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.INDIVIDUAL_CONTACT,
    canActivate: [LoginGuard, canActivateChangeAccountHolderAddNewStep],
    component: IndividualContactDetailsContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.ORGANISATION_DETAILS,
    canActivate: [LoginGuard, canActivateChangeAccountHolderAddNewStep],
    component: OrganisationDetailsContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.ORGANISATION_ADDRESS,
    canActivate: [LoginGuard, canActivateChangeAccountHolderAddNewStep],
    component: OrganisationAddressContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.PRIMARY_CONTACT,
    canActivate: [LoginGuard, canActivateChangeAccountHolderAddNewStep],
    component: ContactDetailsContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.PRIMARY_CONTACT_WORK,
    canActivate: [LoginGuard, canActivateChangeAccountHolderAddNewStep],
    component: ContactWorkDetailsContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.DELETE_ORPHAN_ACCOUNT_HOLDER,
    canActivate: [LoginGuard, canActivateChangeAccountHolderDeleteOrphan],
    component: DeleteOrphanAccountHolderContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.OVERVIEW,
    canActivate: [LoginGuard, canActivateChangeAccountHolderOverview],
    component: ChangeAccountHolderOverviewContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.CANCEL_REQUEST,
    canActivate: [LoginGuard],
    component: CancelContainerComponent,
  },
  {
    path: ChangeAccountHolderWizardPaths.REQUEST_SUBMITTED,
    canActivate: [LoginGuard],
    component: RequestSubmittedContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChangeAccountHolderWizardRoutingModule {}
