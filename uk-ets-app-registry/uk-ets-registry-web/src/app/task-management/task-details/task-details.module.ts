import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { NgModule } from '@angular/core';
import {
  AccountHolderContactsDetailsComponent,
  AccountHolderContactSummaryComponent,
  AccountHolderContactUpdateDetailsContainerComponent,
  AccountHolderMultipleOwnershipComponent,
  AccountHolderUpdateDetailsContainerComponent,
  AccountOpeningTaskDetailsComponent,
  AccountOpeningTaskDetailsContainerComponent,
  AllocationRequestTaskDetailsComponent,
  AllocationTableApprovalConfirmationComponent,
  AllocationTableUploadTaskDetailsComponent,
  AuthorisedRepresentativesUpdateTaskDetailsComponent,
  AuthorisedRepresentativesUpdateTaskDetailsContainerComponent,
  ChangeTokenTaskDetailsComponent,
  CompleteTaskComponent,
  CompleteTaskContainerComponent,
  EmailChangeTaskDetailsComponent,
  EmissionsTableUploadTaskDetailsComponent,
  EnrolmentLetterTaskDetailsComponent,
  GenericTaskDetailsConfirmationComponent,
  GenericTransactionTaskDetailsComponent,
  HistoryCommentsComponent,
  HistoryContainerComponent,
  IssueAllowancesTaskDetailsComponent,
  IssueKpUnitsTaskDetailsComponent,
  LostTokenTaskDetailsComponent,
  RequestAllocationTaskApprovalConfirmationComponent,
  RequestedDocumentsFormComponent,
  RequestedDocumentsFormContainerComponent,
  RequestedDocumentsTaskDetailsComponent,
  TaskApprovalConfirmationComponent,
  TaskCompletionPendingConfirmationComponent,
  TaskApprovalConfirmationContainerComponent,
  TaskCompletionPendingConfirmationContainerComponent,
  TaskDetailsComponent,
  TaskDetailsContainerComponent,
  TaskHeaderComponent,
  TaskUserDetailsContainerComponent,
  TransactionApprovalConfirmationComponent,
  TransactionRulesUpdateTaskDetailsComponent,
  TransactionTaskDetailsComponent,
  TrustedAccountRequestTaskDetailsComponent,
  AccountClosureApprovalConfirmationComponent,
} from '@task-details/components';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { ExportFileService } from '@shared/export-file/export-file.service';
import { TaskDetailsRoutingModule } from '@registry-web/task-management/task-details/task-details-routing.module';
import * as fromTaskDetails from '@registry-web/task-management/task-details/reducers/task-details.reducer';
import {
  TaskDetailsEffects,
  TaskDetailsNavigationEffects,
} from '@task-details/effects';
import { AccountOpeningModule } from '@account-opening/account-opening.module';
import { UserDetailsModule } from '@user-management/user-details/user-details.module';
import { TaskHeaderGuard } from '@task-management/guards/task-header.guard';
import {
  DocumentNamePipe,
  TrustedAccountTaskDescriptionPipe,
  UploadedFilePipe,
} from '@task-management/pipes';
// TODO this should go either into the shared module, or extract from shared everything transaction related to a shared
// transaction module. We are importing a whole module here for a pipe
import { IssueKpUnitsModule } from '@issue-kp-units/issue-kp-units.module';
import { TaskUserDetailsResolver } from '@task-details/task-user-details.resolver';
import { RequestDocumentsModule } from '@request-documents/request-documents.module';
import { SigningModule } from '@signing/signing.module';
import { AccountHolderUpdateService } from '@account-management/account/account-holder-details-wizard/services';
import { OperatorUpdateTaskDetailsComponent } from '@task-details/components/operator-update-task-details';
import { AccountTransferSharedModule } from '@account-transfer-shared/account-transfer-shared.module';
import { AccountTransferTaskDetailsComponent } from '@task-details/components/account-transfer-task-details';
import { OperatorModule } from '@account-opening/operator/operator.module';
import { UserDetailsUpdateTaskDetailsComponent } from './components/user-details-update-task-details/user-details-update-task-details.component';
import { UserDeactivationTaskDetailsComponent } from './components/user-deactivation-task-details/user-deactivation-task-details.component';
import { AccountClosureTaskDetailsComponent } from '@task-details/components/account-closure-task-details';
import {
  IndividualFullNamePipe,
  IndividualPipe,
  OrganisationPipe,
} from '@shared/pipes';
import { TransferringAccountHolderTaskDetailsComponent } from '@task-details/components/transferring-account-holder-task-details';

@NgModule({
  declarations: [
    TaskDetailsContainerComponent,
    TaskDetailsComponent,
    EnrolmentLetterTaskDetailsComponent,
    AccountHolderContactsDetailsComponent,
    HistoryCommentsComponent,
    CompleteTaskContainerComponent,
    CompleteTaskComponent,
    TransactionTaskDetailsComponent,
    HistoryContainerComponent,
    IssueKpUnitsTaskDetailsComponent,
    IssueAllowancesTaskDetailsComponent,
    GenericTransactionTaskDetailsComponent,
    AccountHolderContactSummaryComponent,
    TaskApprovalConfirmationComponent,
    TaskCompletionPendingConfirmationComponent,
    TaskApprovalConfirmationContainerComponent,
    TaskCompletionPendingConfirmationContainerComponent,
    TransactionApprovalConfirmationComponent,
    TrustedAccountRequestTaskDetailsComponent,
    TransactionRulesUpdateTaskDetailsComponent,
    AuthorisedRepresentativesUpdateTaskDetailsComponent,
    TrustedAccountTaskDescriptionPipe,
    UploadedFilePipe,
    DocumentNamePipe,
    AuthorisedRepresentativesUpdateTaskDetailsContainerComponent,
    AllocationTableUploadTaskDetailsComponent,
    AllocationTableApprovalConfirmationComponent,
    AllocationRequestTaskDetailsComponent,
    RequestAllocationTaskApprovalConfirmationComponent,
    RequestedDocumentsTaskDetailsComponent,
    RequestedDocumentsFormContainerComponent,
    RequestedDocumentsFormComponent,
    AccountHolderUpdateDetailsContainerComponent,
    AccountHolderMultipleOwnershipComponent,
    AccountHolderContactUpdateDetailsContainerComponent,
    AccountHolderMultipleOwnershipComponent,
    ChangeTokenTaskDetailsComponent,
    LostTokenTaskDetailsComponent,
    EmailChangeTaskDetailsComponent,
    GenericTaskDetailsConfirmationComponent,
    TaskHeaderComponent,
    TaskUserDetailsContainerComponent,
    AccountOpeningTaskDetailsContainerComponent,
    AccountOpeningTaskDetailsComponent,
    OperatorUpdateTaskDetailsComponent,
    AccountTransferTaskDetailsComponent,
    UserDetailsUpdateTaskDetailsComponent,
    UserDeactivationTaskDetailsComponent,
    EmissionsTableUploadTaskDetailsComponent,
    AccountClosureTaskDetailsComponent,
    AccountClosureApprovalConfirmationComponent,
    TransferringAccountHolderTaskDetailsComponent,
  ],
  imports: [
    TaskDetailsRoutingModule,
    CommonModule,
    FormsModule,
    SharedModule,
    ReactiveFormsModule,
    StoreModule.forFeature(
      fromTaskDetails.taskDetailsFeatureKey,
      fromTaskDetails.reducer
    ),
    EffectsModule.forFeature([
      TaskDetailsEffects,
      TaskDetailsNavigationEffects,
    ]),
    AccountOpeningModule,
    RequestDocumentsModule,
    UserDetailsModule,
    IssueKpUnitsModule,
    SigningModule,
    AccountTransferSharedModule,
    OperatorModule,
  ],
  providers: [
    ExportFileService,
    AccountHolderUpdateService,
    TaskHeaderGuard,
    TaskUserDetailsResolver,
    IndividualPipe,
    IndividualFullNamePipe,
    OrganisationPipe,
  ],
})
export class TaskDetailsModule {}
