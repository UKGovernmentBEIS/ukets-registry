import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AccountStatusRoutingModule } from './account-status-routing.module';
// eslint-disable-next-line max-len
import { SelectAccountStatusActionContainerComponent } from './components/select-account-status-action/select-account-status-action-container.component';
import { SelectAccountStatusActionComponent } from './components/select-account-status-action/select-account-status-action.component';
import { ConfirmAccountStatusActionComponent } from './components/confirm-account-status-action/confirm-account-status-action.component';
// eslint-disable-next-line max-len
import { ConfirmAccountStatusActionContainerComponent } from './components/confirm-account-status-action/confirm-account-status-action-container.component';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    SelectAccountStatusActionContainerComponent,
    SelectAccountStatusActionComponent,
    ConfirmAccountStatusActionComponent,
    ConfirmAccountStatusActionContainerComponent
  ],
  imports: [
    CommonModule,
    AccountStatusRoutingModule,
    SharedModule,
    ReactiveFormsModule
  ]
})
export class AccountStatusModule {}
