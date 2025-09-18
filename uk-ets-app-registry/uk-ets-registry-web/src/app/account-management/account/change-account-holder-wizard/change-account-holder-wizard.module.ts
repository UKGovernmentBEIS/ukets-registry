import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { SharedModule } from '@shared/shared.module';
import { AccountSharedModule } from '@account-shared/account-shared.module';
import { AccountHolderModule } from '@account-opening/account-holder/account-holder.module';
import {
  IndividualFullNamePipe,
  IndividualPipe,
  OrganisationPipe,
} from '@shared/pipes';
import {
  CheckUpdateRequestResolver,
  UpdateTypesResolver,
} from '../account-holder-details-wizard/resolvers';
import { AccountHolderUpdateService } from '../account-holder-details-wizard/services';
import { ChangeAccountHolderWizardRoutingModule } from '@change-account-holder-wizard/change-account-holder-wizard-routing.module';
import {
  ChangeAccountHolderTypeContainerComponent,
  ChangeAccountHolderSelectionContainerComponent,
  ChangeAccountHolderOverviewContainerComponent,
  IndividualDetailsContainerComponent,
  IndividualContactDetailsContainerComponent,
  OrganisationDetailsContainerComponent,
  OrganisationAddressContainerComponent,
  ContactDetailsContainerComponent,
  ContactWorkDetailsContainerComponent,
  CancelContainerComponent,
  RequestSubmittedContainerComponent,
} from '@change-account-holder-wizard/components';
import { ChangeAccountHolderWizardNavigationEffects } from '@change-account-holder-wizard/store/effects';
import * as fromChangeAccountHolderWizard from '@change-account-holder-wizard/store/reducers';

@NgModule({
  declarations: [
    ChangeAccountHolderTypeContainerComponent,
    ChangeAccountHolderSelectionContainerComponent,
    ChangeAccountHolderOverviewContainerComponent,
    IndividualDetailsContainerComponent,
    IndividualContactDetailsContainerComponent,
    OrganisationDetailsContainerComponent,
    OrganisationAddressContainerComponent,
    ContactDetailsContainerComponent,
    ContactWorkDetailsContainerComponent,
    CancelContainerComponent,
    RequestSubmittedContainerComponent,
  ],
  imports: [
    CommonModule,
    ChangeAccountHolderWizardRoutingModule,
    SharedModule,
    ReactiveFormsModule,
    StoreModule.forFeature(
      fromChangeAccountHolderWizard.changeAccountHolderFeatureKey,
      fromChangeAccountHolderWizard.reducer
    ),
    EffectsModule.forFeature([ChangeAccountHolderWizardNavigationEffects]),
    AccountHolderModule,
    AccountSharedModule,
  ],
  providers: [
    UpdateTypesResolver,
    CheckUpdateRequestResolver,
    AccountHolderUpdateService,
    IndividualPipe,
    IndividualFullNamePipe,
    OrganisationPipe,
  ],
})
export class ChangeAccountHolderWizardModule {}
