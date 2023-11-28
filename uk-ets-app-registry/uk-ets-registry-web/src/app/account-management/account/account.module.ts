import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import * as fromAccountDetails from './account-details/account.reducer';
import * as fromAccountStatus from './account-status/store/reducers';
import { EffectsModule } from '@ngrx/effects';
import { AccountEffect } from '@account-management/account/account-details/account.effect';
import { AccountApiService } from '@account-management/service/account-api.service';
import { AccountRoutingModule } from './account-routing.module';
import { OverviewComponent } from '@account-management/account/account-details/overview/overview.component';
import { DetailsComponent } from '@account-management/account/account-details/details/details.component';
import { AccountHolderComponent } from '@account-management/account/account-details/account-holder/account-holder.component';
import {
  AircraftOperatorComponent,
  InstallationComponent,
} from '@account-management/account/account-details/operator';
import { AuthorisedRepresentativesComponent } from '@account-management/account/account-details/authorised-representatives';
import { HistoryAndCommentsContainerComponent } from '@account-management/account/account-details/history-and-comments';
import { HoldingsComponent } from '@account-management/account/account-details/holdings/holdings.component';
import {
  SearchTrustedAccountsFormComponent,
  SearchTrustedAccountsResultsComponent,
  TrustedAccountsComponent,
} from '@account-management/account/account-details/trusted-accounts';
import { UserDetailsModule } from '@user-management/user-details/user-details.module';
import { AccountHeaderGuard } from '@account-management/account/guards/account-header.guard';
import { ReactiveFormsModule } from '@angular/forms';
import { AccountStatusEffects } from './account-status/store/effects/account-status.effects';
import { AccountHoldingDetailsComponent } from '@account-management/account/account-details/holdings/account-holding-details.component';
import { HoldingDetailsResolver } from '@account-management/account/account-details/holdings/holding-details.resolver';
import { AccountBusinessRulesService } from '@account-management/service/account-business-rules.service';
import { AccountDataContainerComponent } from '@registry-web/account-management/account/account-details/components/account-data/account-data-container.component';
import { TransactionProposalModule } from '@transaction-proposal/transaction-proposal.module';
import { AccountUserDetailsResolver } from '@account-management/account/account-details/user-details/account-user-details.resolver';
import { AccountAllocationComponent } from './account-details/account-allocation/account-allocation.component';
import { AllocationTableComponent } from './account-details/account-allocation/allocation-table/allocation-table.component';
import { GroupedNatAndNerAllocationTableComponent } from '@account-management/account/account-details/account-allocation/grouped-allocation-table/grouped-nat-and-ner-allocation-table.component';
import { AllocationWarningsComponent } from './account-details/account-allocation/allocation-warnings/allocation-warnings.component';
import { AccountAllocationService } from '@account-management/service/account-allocation.service';
import {
  TransactionsContainerComponent,
  TransactionsListComponent,
} from '@account-management/account/account-details/transactions';
import { SortService } from '@shared/search/sort/sort.service';
import { RequestDocumentsModule } from '@request-documents/request-documents.module';
import { EditAccountDetailsContainerComponent } from '@account-management/account/account-details/details/edit-account-details-container.component';
import { ConfirmAccountDetailsUpdateContainerComponent } from '@account-management/account/account-details/details/confirm-account-details-update-container.component';
import { AccountDetailsUpdatedContainerComponent } from '@account-management/account/account-details/details/account-details-updated-container.component';
import { CanRequestUpdatePipe } from '@account-management/account/account-details/pipes/can-request-update.pipe';
import { CancelAccountDetailsUpdateContainerComponent } from '@account-management/account/account-details/details/cancel-account-details-update-container.component';
import { AccountWizardsContainerComponent } from './account-wizards-container/account-wizards-container.component';
import { AccountHeaderComponent } from '@account-management/account/headers/account-header/account-header.component';
import { TransactionDetailsModule } from '@transaction-management/transaction-details/transaction-details.module';
import { HoldingsSummaryComponent } from '@account-management/account/account-details/holdings-summary/holdings-summary.component';
import { AccountDataComponent } from '@account-management/account/account-details/components/account-data';
import { EmissionsSurrendersContainerComponent } from '@account-management/account/account-details/components/emissions-surrenders/';
import { EmissionsReportingPeriodComponent } from '@account-management/account/account-details/components/emissions-reporting-period/emissions-reporting-period.component';
import { ComplianceStatusHistoryComponent } from '@account-management/account/account-details/components/compliance-status-history/compliance-status-history.component';
import { ComplianceOverviewComponent } from '@account-management/account/account-details/components/compliance-overview/compliance-overview.component';
import { ZeroAmountToDashPipe } from '@account-management/account/account-details/pipes/zero-amount-to-dash.pipe';
import { NullAmountToDashPipe } from '@account-management/account/account-details/pipes/null-amount-to-dash.pipe';
import { TrustedAccountsEffect } from './account-details/trusted-accounts/trusted-accounts.effects';
import { CancelPendingActivationBannerComponent } from '@trusted-account-list/components/cancel-pending-activation-banner';
import * as fromTrustedAccountList from '@trusted-account-list/reducers';
import { EditBillingDetailsContainerComponent } from './account-details/details/edit-billing-details-container.component';
import { ExcludeBillingContainerComponent } from './account-details/details/exclude-billing-container.component';
import { CancelExcludeBillingContainerComponent } from './account-details/details/cancel-exclude-billing-container.component';
import { ExcludeBillingSucessContainerComponent } from './account-details/details/exclude-billing-success-container.component';
import { NotesEffect } from '@registry-web/notes/store/notes.effects';
import * as fromNotes from '@registry-web/notes/store/notes.reducer';
import { AddNoteFormContainerComponent } from '@registry-web/notes/components/add-note-form/add-note-form-container.component';
import { AddNoteSuccessContainerComponent } from '@registry-web/notes/components/add-note-success/add-note-success-container.component';
import { CancelAddNoteContainerComponent } from '@registry-web/notes/components/cancel-add-note-container/cancel-add-note-container.component';
import { CheckAndConfirmAddNoteContainerComponent } from '@registry-web/notes/components/check-and-confirm-add-note/check-and-confirm-add-note-container.component';
import { DeleteNoteSuccessContainerComponent } from '@registry-web/notes/components/delete-note-success/delete-note-success-container.component';
import { DeleteNoteContainerComponent } from '@registry-web/notes/components/delete-note/delete-note-container.component';
import { SelectEntityFormContainerComponent } from '@registry-web/notes/components/select-entity-form/select-entity-form-container.component';
import { AddNoteFormComponent } from '@registry-web/notes/components/add-note-form/add-note-form.component';
import { AddNoteSuccessComponent } from '@registry-web/notes/components/add-note-success/add-note-success.component';
import { CheckAndConfirmAddNoteComponent } from '@registry-web/notes/components/check-and-confirm-add-note/check-and-confirm-add-note.component';
import { DeleteNoteSuccessComponent } from '@registry-web/notes/components/delete-note-success/delete-note-success.component';
import { DeleteNoteComponent } from '@registry-web/notes/components/delete-note/delete-note.component';
import { SelectEntityFormComponent } from '@registry-web/notes/components/select-entity-form/select-entity-form.component';
import { AccountNotesComponent } from './account-details/notes/account-notes.component';

@NgModule({
  declarations: [
    AccountHeaderComponent,
    AccountDataContainerComponent,
    AccountDataComponent,
    OverviewComponent,
    DetailsComponent,
    EditAccountDetailsContainerComponent,
    EditBillingDetailsContainerComponent,
    ExcludeBillingContainerComponent,
    ConfirmAccountDetailsUpdateContainerComponent,
    AccountDetailsUpdatedContainerComponent,
    CancelAccountDetailsUpdateContainerComponent,
    CancelExcludeBillingContainerComponent,
    ExcludeBillingSucessContainerComponent,
    AccountHolderComponent,
    AircraftOperatorComponent,
    InstallationComponent,
    AuthorisedRepresentativesComponent,
    HistoryAndCommentsContainerComponent,
    HoldingsComponent,
    HoldingsSummaryComponent,
    TransactionsListComponent,
    TrustedAccountsComponent,
    AccountNotesComponent,
    SearchTrustedAccountsFormComponent,
    SearchTrustedAccountsResultsComponent,
    AccountHoldingDetailsComponent,
    AccountAllocationComponent,
    AllocationTableComponent,
    GroupedNatAndNerAllocationTableComponent,
    AllocationWarningsComponent,
    TransactionsContainerComponent,
    AccountWizardsContainerComponent,
    EmissionsSurrendersContainerComponent,
    EmissionsReportingPeriodComponent,
    ComplianceStatusHistoryComponent,
    ComplianceOverviewComponent,
    CancelPendingActivationBannerComponent,
    CanRequestUpdatePipe,
    ZeroAmountToDashPipe,
    NullAmountToDashPipe,
    SelectEntityFormContainerComponent,
    AddNoteFormContainerComponent,
    CheckAndConfirmAddNoteContainerComponent,
    AddNoteSuccessContainerComponent,
    CancelAddNoteContainerComponent,
    DeleteNoteContainerComponent,
    DeleteNoteSuccessContainerComponent,
    SelectEntityFormComponent,
    AddNoteFormComponent,
    CheckAndConfirmAddNoteComponent,
    AddNoteSuccessComponent,
    DeleteNoteComponent,
    DeleteNoteSuccessComponent,
  ],
  imports: [
    AccountRoutingModule,
    CommonModule,
    SharedModule,
    StoreModule.forFeature(
      fromAccountDetails.accountFeatureKey,
      fromAccountDetails.reducer
    ),
    StoreModule.forFeature(
      fromAccountStatus.accountStatusFeatureKey,
      fromAccountStatus.reducer
    ),
    StoreModule.forFeature(
      fromTrustedAccountList.trustedAccountListFeatureKey,
      fromTrustedAccountList.reducer
    ),
    StoreModule.forFeature(fromNotes.notesFeatureKey, fromNotes.reducer),
    EffectsModule.forFeature([NotesEffect]),
    EffectsModule.forFeature([AccountEffect]),
    EffectsModule.forFeature([AccountStatusEffects, TrustedAccountsEffect]),
    ReactiveFormsModule,
    UserDetailsModule,
    TransactionProposalModule,
    RequestDocumentsModule,
    TransactionDetailsModule,
  ],
  providers: [
    AccountApiService,
    AccountHeaderGuard,
    HoldingDetailsResolver,
    AccountBusinessRulesService,
    AccountUserDetailsResolver,
    AccountAllocationService,
    SortService,
  ],
})
export class AccountModule {}
