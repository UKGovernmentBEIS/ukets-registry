import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  CancelChangeContactInvitationContainerComponent,
  ChangeContactInvitationOverviewContainerComponent,
  ConfirmChangeContactInvitationContainerComponent,
  SelectContactsContainerComponent,
} from '@change-contact-invitation-wizard/components';
import { LoginGuard } from '@shared/guards/login.guard';

export const CHANGE_CONTACT_INVITATION_BASE_PATH = 'change-contact-invitation';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: SelectContactsContainerComponent,
  },
  {
    path: 'select-contacts',
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: 'overview',
    canActivate: [LoginGuard],
    component: ChangeContactInvitationOverviewContainerComponent,
  },
  {
    path: 'cancel-request',
    canActivate: [LoginGuard],
    component: CancelChangeContactInvitationContainerComponent,
  },
  {
    path: 'request-submitted',
    canActivate: [LoginGuard],
    component: ConfirmChangeContactInvitationContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChangeContactInvitationWizardRoutingModule {}
