import { LoginGuard } from '@shared/guards';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { ClearTransactionProposalGuard } from '@transaction-proposal/guards/clear-transaction-proposal-guard.service';
import { ClearUpdateTrustedAccountGuard } from '@trusted-account-list/guards/clear-update-trusted-account-guard.service';
import { AccountHoldingDetailsComponent } from '@account-management/account/account-details/holdings/account-holding-details.component';
import { HoldingDetailsResolver } from '@account-management/account/account-details/holdings/holding-details.resolver';
import { AccountDataContainerComponent } from '@registry-web/account-management/account/account-details/components/account-data/account-data-container.component';
import { ClearUpdateAuthorisedRepresentativesGuard } from '@authorised-representatives/guards';
import { ClearUpdateTalTransactionRulesGuard } from '@tal-transaction-rules/guards/clear-update-tal-transaction-rules-guard.service';
import { UserDetailsContainerComponent } from '@user-management/user-details/components';
import { AccountUserDetailsResolver } from '@account-management/account/account-details/user-details/account-user-details.resolver';
import { AllocationStatusRoutePathsModel } from '@allocation-status/model/allocation-status-route-paths.model';
import { ClearUpdateAccountHolderDetailsGuard } from '@account-management/account/account-holder-details-wizard/guards';
import { AccountHolderDetailsWizardPathsModel } from '@account-management/account/account-holder-details-wizard/model';
import { EditAccountDetailsContainerComponent } from '@account-management/account/account-details/details/edit-account-details-container.component';
import { ConfirmAccountDetailsUpdateContainerComponent } from '@account-management/account/account-details/details/confirm-account-details-update-container.component';
import { AccountDetailsUpdatedContainerComponent } from '@account-management/account/account-details/details/account-details-updated-container.component';
import { CancelAccountDetailsUpdateContainerComponent } from '@account-management/account/account-details/details/cancel-account-details-update-container.component';
import { AccountWizardsContainerComponent } from '@account-management/account/account-wizards-container/account-wizards-container.component';
import { AccountTransferPathsModel } from '@account-transfer/model';
import { ClearAccountTransferRequestGuard } from '@account-transfer/guards';
import { ClearUpdateOperatorGuard } from '@operator-update/guards/clear-update-operator-guard';
import { AccountClosureWizardPathsModel } from '@account-management/account/account-closure-wizard/models';
import { UpdateExclusionStatusPathsModel } from '@exclusion-status-update-wizard/model/update-exclusion-status-paths.model';
import { ClearExclusionStatusGuard } from '@exclusion-status-update-wizard/guards';
import { EditBillingDetailsContainerComponent } from './account-details/details/edit-billing-details-container.component';
import { ExcludeBillingContainerComponent } from './account-details/details/exclude-billing-container.component';
import { CancelExcludeBillingContainerComponent } from './account-details/details/cancel-exclude-billing-container.component';
import { ExcludeBillingSucessContainerComponent } from './account-details/details/exclude-billing-success-container.component';
import { CHANGE_ACCOUNT_HOLDER_BASE_PATH } from '@change-account-holder-wizard/model';
import {
  canActivateChangeAccountHolder,
  clearChangeAccountHolderRequestGuard,
  IsAccountHolderOrphanResolver,
} from '@change-account-holder-wizard/guards-and-resolvers';
import { SEND_INVITATION_BASE_PATH } from '@send-invitation-wizard/send-invitation-wizard.helpers';
import {
  canActivateSendInvitation,
  clearSendInvitationGuard,
} from '@send-invitation-wizard/send-invitation-wizard.guards';
import { AUTO_CLAIM_BASE_PATH } from '@auto-claim-wizard/auto-claim-wizard-routing.module';
import { initAccountDetailsResolver } from '@account-management/account/resolvers';
import { CHANGE_CONTACT_INVITATION_BASE_PATH } from '@change-contact-invitation-wizard/change-contact-invitation-wizard-routing.module';
import { clearChangeContactInvitationGuard } from '@change-contact-invitation-wizard/guards';

const routes: Routes = [
  {
    path: ':accountId',
    canActivate: [LoginGuard],
    resolve: {
      header: initAccountDetailsResolver,
    },
    component: AccountDataContainerComponent,
    title: 'Account Details',
  },
  {
    path: '',
    component: AccountWizardsContainerComponent,
    canActivate: [LoginGuard],
    children: [
      {
        path: ':accountId/edit-confirm',
        resolve: {
          header: initAccountDetailsResolver,
        },
        component: ConfirmAccountDetailsUpdateContainerComponent,
      },
      {
        path: ':accountId/cancel-update',
        resolve: {
          header: initAccountDetailsResolver,
        },
        component: CancelAccountDetailsUpdateContainerComponent,
      },
      {
        path: ':accountId/updated',
        resolve: {
          header: initAccountDetailsResolver,
        },
        component: AccountDetailsUpdatedContainerComponent,
      },
      {
        path: ':accountId/holdings/details',
        component: AccountHoldingDetailsComponent,
        resolve: {
          header: initAccountDetailsResolver,
          details: HoldingDetailsResolver,
        },
      },
      {
        path: ':accountId/status',
        resolve: {
          header: initAccountDetailsResolver,
        },
        loadChildren: () =>
          import('./account-status/account-status.module').then(
            (m) => m.AccountStatusModule
          ),
      },
      {
        path: ':accountId/user/:urid',
        resolve: {
          header: initAccountDetailsResolver,
          user: AccountUserDetailsResolver,
        },
        component: UserDetailsContainerComponent,
      },
      {
        path: ':accountId/edit',
        resolve: {
          header: initAccountDetailsResolver,
        },
        component: EditAccountDetailsContainerComponent,
      },
      {
        path: ':accountId/edit-billing',
        resolve: {
          header: initAccountDetailsResolver,
        },
        component: EditBillingDetailsContainerComponent,
      },
      {
        path: ':accountId/exclude-billing',
        resolve: {
          header: initAccountDetailsResolver,
        },
        component: ExcludeBillingContainerComponent,
      },
      {
        path: ':accountId/cancel-exclude-billing',
        resolve: {
          header: initAccountDetailsResolver,
        },
        component: CancelExcludeBillingContainerComponent,
      },
      {
        path: ':accountId/exclude-billing-success',
        resolve: {
          header: initAccountDetailsResolver,
        },
        component: ExcludeBillingSucessContainerComponent,
      },
      {
        path: ':accountId/notes',
        resolve: {
          header: initAccountDetailsResolver,
        },
        loadChildren: () =>
          import(
            './account-details/notes/account-notes-wizard/account-notes-wizard-routing.module'
          ).then((m) => m.AccountNotesRoutingModule),
      },
      {
        path: ':accountId/transactions',
        resolve: {
          header: initAccountDetailsResolver,
        },
        canDeactivate: [ClearTransactionProposalGuard],
        loadChildren: () =>
          import('@transaction-proposal/transaction-proposal.module').then(
            (m) => m.TransactionProposalModule
          ),
      },
      {
        path: ':accountId/' + AccountHolderDetailsWizardPathsModel.BASE_PATH,
        resolve: {
          header: initAccountDetailsResolver,
        },
        canDeactivate: [ClearUpdateAccountHolderDetailsGuard],
        loadChildren: () =>
          import(
            './account-holder-details-wizard/account-holder-details-wizard.module'
          ).then((m) => m.AccountHolderDetailsWizardModule),
      },
      {
        path: ':accountId/' + CHANGE_ACCOUNT_HOLDER_BASE_PATH,
        canDeactivate: [clearChangeAccountHolderRequestGuard],
        canActivate: [canActivateChangeAccountHolder],
        resolve: {
          initAccountDetailsResolver,
          isAccountHolderOrphan: IsAccountHolderOrphanResolver,
        },
        loadChildren: () =>
          import(
            './change-account-holder-wizard/change-account-holder-wizard.module'
          ).then((m) => m.ChangeAccountHolderWizardModule),
      },
      {
        path: ':accountId/' + SEND_INVITATION_BASE_PATH,
        resolve: {
          header: initAccountDetailsResolver,
        },
        canDeactivate: [clearSendInvitationGuard],
        canActivate: [canActivateSendInvitation],
        loadChildren: () =>
          import('./send-invitation-wizard/send-invitation-wizard.module').then(
            (m) => m.SendInvitationWizardModule
          ),
      },
      {
        path: ':accountId/' + CHANGE_CONTACT_INVITATION_BASE_PATH,
        resolve: {
          header: initAccountDetailsResolver,
        },
        canDeactivate: [clearChangeContactInvitationGuard],
        // canActivate: [canActivateSendInvitation],
        loadChildren: () =>
          import(
            './change-contact-invitation-wizard/change-contact-invitation-wizard.module'
          ).then((m) => m.ChangeContactInvitationWizardModule),
      },
      {
        path: ':accountId/' + AUTO_CLAIM_BASE_PATH,
        resolve: {
          header: initAccountDetailsResolver,
        },
        loadChildren: () =>
          import('./auto-claim-wizard/auto-claim-wizard.module').then(
            (m) => m.AutoClaimWizardModule
          ),
      },
      {
        path: ':accountId/tal-transaction-rules',
        resolve: {
          header: initAccountDetailsResolver,
        },
        canDeactivate: [ClearUpdateTalTransactionRulesGuard],
        loadChildren: () =>
          import('./tal-transaction-rules/tal-transaction-rules.module').then(
            (m) => m.TalTransactionRulesModule
          ),
      },
      {
        path: ':accountId/operator-update',
        resolve: {
          header: initAccountDetailsResolver,
        },
        canDeactivate: [ClearUpdateOperatorGuard],
        loadChildren: () =>
          import('./operator-update-wizard/operator-update-wizard.module').then(
            (m) => m.OperatorUpdateWizardModule
          ),
      },
      {
        path: ':accountId/trusted-account-list',
        resolve: {
          header: initAccountDetailsResolver,
        },
        canDeactivate: [ClearUpdateTrustedAccountGuard],
        loadChildren: () =>
          import('./trusted-account-list/trusted-account-list.module').then(
            (m) => m.TrustedAccountListModule
          ),
      },
      {
        path: ':accountId/authorised-representatives',
        resolve: {
          header: initAccountDetailsResolver,
        },
        canDeactivate: [ClearUpdateAuthorisedRepresentativesGuard],
        loadChildren: () =>
          import(
            './authorised-representatives/authorised-representatives.module'
          ).then((m) => m.AuthorisedRepresentativesModule),
      },
      {
        path: `:accountId/${AllocationStatusRoutePathsModel.ALLOCATION_STATUS}`,
        resolve: {
          header: initAccountDetailsResolver,
        },
        loadChildren: () =>
          import('./allocation-status/allocation-status.module').then(
            (m) => m.AllocationStatusModule
          ),
      },
      {
        path: `:accountId/${AccountTransferPathsModel.BASE_PATH}`,
        resolve: {
          header: initAccountDetailsResolver,
        },
        canDeactivate: [ClearAccountTransferRequestGuard],
        loadChildren: () =>
          import('./account-transfer/account-transfer.module').then(
            (m) => m.AccountTransferModule
          ),
      },
      {
        path: `:accountId/${AccountClosureWizardPathsModel.BASE_PATH}`,
        resolve: {
          header: initAccountDetailsResolver,
        },
        loadChildren: () =>
          import('./account-closure-wizard/account-closure-wizard.module').then(
            (m) => m.AccountClosureWizardModule
          ),
      },
      {
        path: `:accountId/${UpdateExclusionStatusPathsModel.BASE_PATH}`,
        resolve: {
          header: initAccountDetailsResolver,
        },
        canDeactivate: [ClearExclusionStatusGuard],
        loadChildren: () =>
          import(
            './exclusion-status-update-wizard/exclusion-status-update-wizard.module'
          ).then((m) => m.ExclusionStatusUpdateWizardModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountRoutingModule {}
