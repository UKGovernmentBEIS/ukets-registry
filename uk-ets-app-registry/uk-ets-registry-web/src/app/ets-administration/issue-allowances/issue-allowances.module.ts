import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  SpecifyAllowanceQuantityComponent,
  SpecifyAllowanceQuantityContainerComponent,
  AllowancesProposalSubmittedContainerComponent,
  CheckAllowancesRequestComponent,
  CheckAllowancesRequestContainerComponent,
  AllowancesProposalSubmittedComponent,
  CancelAllowancesIssuanceProposalContainerComponent,
  SetTransactionReferenceContainerComponent,
} from './components';
import { IssueAllowancesRoutingModule } from './issue-allowances-routing.module';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { StoreModule } from '@ngrx/store';
import * as fromIssueKpReducer from './reducers';
import { EffectsModule } from '@ngrx/effects';
import { IssueAllowancesEffects } from '@issue-allowances/effects';
import { SigningModule } from '@signing/signing.module';

@NgModule({
  declarations: [
    SpecifyAllowanceQuantityComponent,
    SpecifyAllowanceQuantityContainerComponent,
    CheckAllowancesRequestComponent,
    CheckAllowancesRequestContainerComponent,
    AllowancesProposalSubmittedComponent,
    AllowancesProposalSubmittedContainerComponent,
    CancelAllowancesIssuanceProposalContainerComponent,
    SetTransactionReferenceContainerComponent,
  ],
  imports: [
    IssueAllowancesRoutingModule,
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    SigningModule,
    StoreModule.forFeature(
      fromIssueKpReducer.issueAllowanceReducer,
      fromIssueKpReducer.reducer
    ),
    EffectsModule.forFeature([IssueAllowancesEffects]),
  ],
})
export class IssueAllowancesModule {}
