import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@registry-web/shared/guards';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { SharedModule } from '@shared/shared.module';
import { SendInvitationWizardPaths } from '@send-invitation-wizard/send-invitation-wizard.helpers';
import {
  SelectContactsContainerComponent,
  SendInvitationOverviewComponent,
  RequestSubmittedContainerComponent,
  CancelContainerComponent,
} from '@send-invitation-wizard/components';
import {
  SendInvitationWizardEffects,
  sendInvitationWizardFeature,
} from '@send-invitation-wizard/store';
import { canActivateSendInvitationOverview } from '@send-invitation-wizard/send-invitation-wizard.guards';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: SelectContactsContainerComponent,
  },
  {
    path: SendInvitationWizardPaths.SELECT_CONTACTS,
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: SendInvitationWizardPaths.OVERVIEW,
    canActivate: [LoginGuard, canActivateSendInvitationOverview],
    component: SendInvitationOverviewComponent,
  },
  {
    path: SendInvitationWizardPaths.CANCEL_REQUEST,
    canActivate: [LoginGuard],
    component: CancelContainerComponent,
  },
  {
    path: SendInvitationWizardPaths.REQUEST_SUBMITTED,
    canActivate: [LoginGuard],
    component: RequestSubmittedContainerComponent,
  },
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedModule,
    ReactiveFormsModule,
    StoreModule.forFeature(sendInvitationWizardFeature),
    EffectsModule.forFeature([SendInvitationWizardEffects]),
  ],
})
export class SendInvitationWizardModule {}
