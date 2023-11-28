import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import * as fromIssueKpReducer from './reducers';
import { EffectsModule } from '@ngrx/effects';
import {
  IssueKpUnitsEffects,
  IssueKpUnitsNavigationEffects,
} from '@issue-kp-units/effects';
import { IssueKpUnitsRoutingModule } from './issue-kp-units-routing.module';
import {
  CheckRequestAndSignComponent,
  CheckRequestAndSignContainerComponent,
  ProposalSubmittedComponent,
  ProposalSubmittedContainerComponent,
  SelectCommitmentPeriodAccountComponent,
  SelectCommitmentPeriodAccountContainerComponent,
  SelectUnitTypeComponent,
  SelectUnitTypeContainerComponent,
  SetTransactionReferenceContainerComponent,
} from '@issue-kp-units/components';
import { ReactiveFormsModule } from '@angular/forms';
import {
  RemainingQuantityPipe,
  UnitTypeAndActivityPipe,
} from '@issue-kp-units/pipes';
import { SigningModule } from '@signing/signing.module';

@NgModule({
  declarations: [
    SelectCommitmentPeriodAccountContainerComponent,
    SelectCommitmentPeriodAccountComponent,
    SelectUnitTypeComponent,
    SelectUnitTypeContainerComponent,
    CheckRequestAndSignContainerComponent,
    CheckRequestAndSignComponent,
    ProposalSubmittedContainerComponent,
    SetTransactionReferenceContainerComponent,
    ProposalSubmittedComponent,
    UnitTypeAndActivityPipe,
    RemainingQuantityPipe,
  ],
  imports: [
    IssueKpUnitsRoutingModule,
    CommonModule,
    SharedModule,
    StoreModule.forFeature(
      fromIssueKpReducer.issueKpUnitsFeatureKey,
      fromIssueKpReducer.reducer
    ),
    EffectsModule.forFeature([
      IssueKpUnitsEffects,
      IssueKpUnitsNavigationEffects,
    ]),
    ReactiveFormsModule,
    SigningModule,
  ],
  exports: [
    SelectCommitmentPeriodAccountContainerComponent,
    UnitTypeAndActivityPipe,
  ],
  providers: [UnitTypeAndActivityPipe],
})
export class IssueKpUnitsModule {}
