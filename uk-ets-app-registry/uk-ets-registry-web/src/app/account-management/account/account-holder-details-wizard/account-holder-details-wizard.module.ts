import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import {
  CheckUpdateRequestResolver,
  UpdateTypesResolver,
} from '@account-management/account/account-holder-details-wizard/resolvers';
import { EffectsModule } from '@ngrx/effects';
import { AccountHolderDetailsWizardNavigationEffects } from '@account-management/account/account-holder-details-wizard/effects';
import * as fromAccountHolderDetailsWizard from './reducers';
import { StoreModule } from '@ngrx/store';
import { AccountModule } from '@account-management/account/account.module';
import { AccountHolderUpdateService } from '@account-management/account/account-holder-details-wizard/services';
import { AccountHolderUpdatePipe } from '@account-management/account/account-holder-details-wizard/pipes';
import { AccountHolderDetailsWizardRoutingModule } from './account-holder-details-wizard-routing.module';
import {
  CancelUpdateRequestContainerComponent,
  CheckUpdateRequestComponent,
  CheckUpdateRequestContainerComponent,
  RequestSubmittedContainerComponent,
  SelectAccountHolderUpdateTypeComponent,
  SelectAccountHolderUpdateTypeContainerComponent,
  UpdateAccountHolderDetailsAddressContainerComponent,
  UpdateAccountHolderDetailsContainerComponent,
  UpdatePrimaryContactDetailsContainerComponent,
  UpdatePrimaryContactWorkDetailsContainerComponent,
  UpdateAlternativePrimaryContactDetailsContainerComponent,
  UpdateAlternativePrimaryContactWorkDetailsContainerComponent,
} from '@account-management/account/account-holder-details-wizard/components';
import {
  IndividualFullNamePipe,
  IndividualPipe,
  OrganisationPipe,
} from '@shared/pipes';

@NgModule({
  declarations: [
    SelectAccountHolderUpdateTypeComponent,
    SelectAccountHolderUpdateTypeContainerComponent,
    CancelUpdateRequestContainerComponent,
    UpdateAccountHolderDetailsContainerComponent,
    UpdatePrimaryContactDetailsContainerComponent,
    UpdatePrimaryContactWorkDetailsContainerComponent,
    UpdateAccountHolderDetailsAddressContainerComponent,
    UpdateAlternativePrimaryContactDetailsContainerComponent,
    UpdateAlternativePrimaryContactWorkDetailsContainerComponent,
    CheckUpdateRequestContainerComponent,
    CheckUpdateRequestComponent,
    RequestSubmittedContainerComponent,
    AccountHolderUpdatePipe,
  ],
  imports: [
    CommonModule,
    AccountHolderDetailsWizardRoutingModule,
    SharedModule,
    ReactiveFormsModule,
    StoreModule.forFeature(
      fromAccountHolderDetailsWizard.accountHolderDetailsWizardFeatureKey,
      fromAccountHolderDetailsWizard.reducer
    ),
    EffectsModule.forFeature([AccountHolderDetailsWizardNavigationEffects]),
    AccountModule,
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
export class AccountHolderDetailsWizardModule {}
