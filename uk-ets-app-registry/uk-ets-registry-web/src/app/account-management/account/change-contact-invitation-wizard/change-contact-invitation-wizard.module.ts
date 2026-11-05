import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ChangeContactInvitationWizardRoutingModule } from '@change-contact-invitation-wizard/change-contact-invitation-wizard-routing.module';
import {
  SelectContactsContainerComponent,
  ChangeContactInvitationOverviewContainerComponent,
  CancelChangeContactInvitationContainerComponent,
  ConfirmChangeContactInvitationContainerComponent,
} from '@change-contact-invitation-wizard/components';
import { EffectsModule } from '@ngrx/effects';
import * as fromChangeContactInvitationWizard from '@change-contact-invitation-wizard/store/reducers';
import { ChangeContactInvitationWizardEffects } from '@change-contact-invitation-wizard/store';
import { StoreModule } from '@ngrx/store';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { MultiSelectTableComponent } from '@shared/components/multi-select-table';
import {
  MetsContactOperatorTypePipe,
  MetsContactTypePipe,
  RegistryContactTypePipe,
} from '@shared/pipes';
import { AccountContactsTablesComponent } from '@shared/components/account/account-contacts-tables/';

@NgModule({
  declarations: [
    SelectContactsContainerComponent,
    ChangeContactInvitationOverviewContainerComponent,
    CancelChangeContactInvitationContainerComponent,
    ConfirmChangeContactInvitationContainerComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    MultiSelectTableComponent,
    AccountContactsTablesComponent,
    MetsContactTypePipe,
    MetsContactOperatorTypePipe,
    RegistryContactTypePipe,
    ChangeContactInvitationWizardRoutingModule,
    StoreModule.forFeature(
      fromChangeContactInvitationWizard.changeContactInvitationWizardFeatureKey,
      fromChangeContactInvitationWizard.reducer
    ),
    EffectsModule.forFeature([ChangeContactInvitationWizardEffects]),
  ],
})
export class ChangeContactInvitationWizardModule {}
