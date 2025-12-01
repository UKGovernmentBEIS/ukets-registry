import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AccountTransferRoutingModule } from '@account-transfer/account-transfer-routing.module';
import {
  SelectAccountTransferAcquiringAccountHolderComponent,
  SelectAccountTransferAcquiringAccountHolderContainerComponent,
} from '@account-transfer/components/select-account-transfer-acquiring-account-holder';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { AccountTransferNavigationEffects } from '@account-transfer/store/effects';
import * as fromAccountTransfer from '@account-transfer/store/reducers';
import { AccountTransferService } from '@account-transfer/services';
import { CancelAccountTransferContainerComponent } from '@account-transfer/components/cancel-account-transfer/cancel-account-transfer-container.component';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { CheckAccountTransferComponent } from '@account-transfer/components/check-account-transfer/check-account-transfer.component';
import { CheckAccountTransferContainerComponent } from '@account-transfer/components/check-account-transfer/check-account-transfer-container.component';
import { SubmittedAccountTransferContainerComponent } from '@account-transfer/components/submitted-account-transfer/submitted-account-transfer-container.component';
import { AccountTransferSharedModule } from '@account-transfer-shared/account-transfer-shared.module';
import { AcquiringOrganisationDetailsContainerComponent } from '@account-transfer/components/acquiring-organisation-details/acquiring-organisation-details-container.component';
import { AcquiringOrganisationDetailsAddressContainerComponent } from '@account-transfer/components/acquiring-organisation-details-address/acquiring-organisation-details-address-container.component';
import { AcquiringPrimaryContactDetailsContainerComponent } from '@account-transfer/components/acquiring-primary-contact-details/acquiring-primary-contact-details-container.component';
import { AcquiringPrimaryContactWorkDetailsContainerComponent } from '@account-transfer/components/acquiring-primary-contact-work-details/acquiring-primary-contact-work-details-container.component';
import { AccountHolderSharedModule } from '@registry-web/account-shared/account-holder-shared.module';
import { AccountTransferRequestSubmittedComponent } from '@account-transfer/components/account-transfer-request-submitted';

@NgModule({
  declarations: [
    SelectAccountTransferAcquiringAccountHolderComponent,
    SelectAccountTransferAcquiringAccountHolderContainerComponent,
    CancelAccountTransferContainerComponent,
    CheckAccountTransferComponent,
    CheckAccountTransferContainerComponent,
    SubmittedAccountTransferContainerComponent,
    AcquiringOrganisationDetailsContainerComponent,
    AcquiringOrganisationDetailsAddressContainerComponent,
    AcquiringPrimaryContactDetailsContainerComponent,
    AcquiringPrimaryContactWorkDetailsContainerComponent,
    AccountTransferRequestSubmittedComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    AccountTransferSharedModule,
    NgbTypeaheadModule,
    ReactiveFormsModule,
    AccountTransferRoutingModule,
    AccountHolderSharedModule,
    StoreModule.forFeature(
      fromAccountTransfer.accountTransferFeatureKey,
      fromAccountTransfer.reducer
    ),
    EffectsModule.forFeature([AccountTransferNavigationEffects]),
  ],
  providers: [AccountTransferService],
})
export class AccountTransferModule {}
