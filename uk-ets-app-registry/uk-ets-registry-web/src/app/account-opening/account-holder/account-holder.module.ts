import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AccountHolderTypeComponent,
  AccountHolderTypeContainerComponent,
} from '@account-opening/account-holder/';
import { RouterModule } from '@angular/router';
import {
  ACCOUNT_HOLDER_ROUTES,
  AccountHolderSelectionComponent,
  AccountHolderSelectionContainerComponent,
  IndividualContactDetailsContainerComponent,
  IndividualDetailsContainerComponent,
  OrganisationAddressContainerComponent,
  OrganisationDetailsContainerComponent,
  OverviewComponent,
} from '@account-opening/account-holder';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { AccountOpeningModule } from '../account-opening.module';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { AccountHolderSharedModule } from '@registry-web/account-shared/account-holder-shared.module';
import { AccountHolderService } from './account-holder.service';

@NgModule({
  declarations: [
    AccountHolderTypeComponent,
    AccountHolderTypeContainerComponent,
    AccountHolderSelectionComponent,
    AccountHolderSelectionContainerComponent,
    IndividualDetailsContainerComponent,
    IndividualContactDetailsContainerComponent,
    OrganisationDetailsContainerComponent,
    OrganisationAddressContainerComponent,
    OverviewComponent,
  ],
  imports: [
    RouterModule.forChild(ACCOUNT_HOLDER_ROUTES),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedModule,
    AccountOpeningModule,
    NgbTypeaheadModule,
    AccountHolderSharedModule,
  ],
  exports: [AccountHolderTypeComponent, AccountHolderSelectionComponent],
})
export class AccountHolderModule {}
