import { LoginGuard } from '@shared/guards';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { AccountHeaderGuard } from './guards/account-header.guard';
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
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model';
import { clearChangeAccountHolderRequestGuard } from '@change-account-holder-wizard/guards';

const routes: Routes = [
  {
    path: ':accountId',
    canActivate: [LoginGuard, AccountHeaderGuard],
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
        component: ConfirmAccountDetailsUpdateContainerComponent,
      },
      {
        path: ':accountId/cancel-update',
        component: CancelAccountDetailsUpdateContainerComponent,
      },
      {
        path: ':accountId/updated',
        canDeactivate: [AccountHeaderGuard],
        component: AccountDetailsUpdatedContainerComponent,
      },
      {
        path: ':accountId/holdings/details',
        component: AccountHoldingDetailsComponent,
        resolve: {
          details: HoldingDetailsResolver,
        },
      },
      {
        path: ':accountId/status',
        canDeactivate: [AccountHeaderGuard],
        loadChildren: () =>
          import('./account-status/account-status.module').then(
            (m) => m.AccountStatusModule
          ),
      },
      {
        path: ':accountId/user/:urid',
        resolve: { user: AccountUserDetailsResolver },
        component: UserDetailsContainerComponent,
      },
      {
        path: ':accountId/edit',
        component: EditAccountDetailsContainerComponent,
      },
      {
        path: ':accountId/edit-billing',
        component: EditBillingDetailsContainerComponent,
      },
      {
        path: ':accountId/exclude-billing',
        component: ExcludeBillingContainerComponent,
      },
      {
        path: ':accountId/cancel-exclude-billing',
        component: CancelExcludeBillingContainerComponent,
      },
      {
        path: ':accountId/exclude-billing-success',
        component: ExcludeBillingSucessContainerComponent,
      },
      {
        path: ':accountId/notes',
        canDeactivate: [AccountHeaderGuard],
        loadChildren: () =>
          import(
            './account-details/notes/account-notes-wizard/account-notes-wizard-routing.module'
          ).then((m) => m.AccountNotesRoutingModule),
      },
      {
        path: ':accountId/transactions',
        canDeactivate: [ClearTransactionProposalGuard],
        loadChildren: () =>
          import('@transaction-proposal/transaction-proposal.module').then(
            (m) => m.TransactionProposalModule
          ),
      },
      {
        path: ':accountId/' + AccountHolderDetailsWizardPathsModel.BASE_PATH,
        canDeactivate: [ClearUpdateAccountHolderDetailsGuard],
        loadChildren: () =>
          import(
            './account-holder-details-wizard/account-holder-details-wizard.module'
          ).then((m) => m.AccountHolderDetailsWizardModule),
      },
      {
        path: ':accountId/' + ChangeAccountHolderWizardPathsModel.BASE_PATH,
        canDeactivate: [clearChangeAccountHolderRequestGuard],
        loadChildren: () =>
          import(
            './change-account-holder-wizard/change-account-holder-wizard.module'
          ).then((m) => m.ChangeAccountHolderWizardModule),
      },
      {
        path: ':accountId/tal-transaction-rules',
        canDeactivate: [ClearUpdateTalTransactionRulesGuard],
        loadChildren: () =>
          import('./tal-transaction-rules/tal-transaction-rules.module').then(
            (m) => m.TalTransactionRulesModule
          ),
      },
      {
        path: ':accountId/operator-update',
        canDeactivate: [ClearUpdateOperatorGuard],
        loadChildren: () =>
          import('./operator-update-wizard/operator-update-wizard.module').then(
            (m) => m.OperatorUpdateWizardModule
          ),
      },
      {
        path: ':accountId/trusted-account-list',
        canDeactivate: [ClearUpdateTrustedAccountGuard],
        loadChildren: () =>
          import('./trusted-account-list/trusted-account-list.module').then(
            (m) => m.TrustedAccountListModule
          ),
      },
      {
        path: ':accountId/authorised-representatives',
        canDeactivate: [ClearUpdateAuthorisedRepresentativesGuard],
        loadChildren: () =>
          import(
            './authorised-representatives/authorised-representatives.module'
          ).then((m) => m.AuthorisedRepresentativesModule),
      },
      {
        path: `:accountId/${AllocationStatusRoutePathsModel.ALLOCATION_STATUS}`,
        loadChildren: () =>
          import('./allocation-status/allocation-status.module').then(
            (m) => m.AllocationStatusModule
          ),
      },
      {
        path: `:accountId/${AccountTransferPathsModel.BASE_PATH}`,
        canDeactivate: [ClearAccountTransferRequestGuard],
        loadChildren: () =>
          import('./account-transfer/account-transfer.module').then(
            (m) => m.AccountTransferModule
          ),
      },
      {
        path: `:accountId/${AccountClosureWizardPathsModel.BASE_PATH}`,
        loadChildren: () =>
          import('./account-closure-wizard/account-closure-wizard.module').then(
            (m) => m.AccountClosureWizardModule
          ),
      },
      {
        path: `:accountId/${UpdateExclusionStatusPathsModel.BASE_PATH}`,
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
