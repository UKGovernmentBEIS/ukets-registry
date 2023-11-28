import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';

import {
  EmailChangeVerificationComponent,
  EmailChangeVerificationContainerComponent,
  NewEmailFormComponent,
  NewEmailFormContainerComponent,
  EmailChangeConfirmationContainerComponent
} from '@email-change/component';
import { EmailChangeRoutingModule } from '@email-change/email-change-routing.module';
import { EmailChangeStoreModule } from '@email-change/email-change-store.module';
import { EmailChangeConfirmationComponent } from '@email-change/component/confirmation/email-change-confirmation.component';
import { EmailChangeConfirmationResolver } from '@email-change/resolver/email-change-confirmation.resolver';

@NgModule({
  declarations: [
    NewEmailFormComponent,
    NewEmailFormContainerComponent,
    EmailChangeVerificationComponent,
    EmailChangeVerificationContainerComponent,
    EmailChangeConfirmationComponent,
    EmailChangeConfirmationContainerComponent
  ],
  imports: [
    EmailChangeRoutingModule,
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    EmailChangeStoreModule
  ],
  providers: [EmailChangeConfirmationResolver]
})
export class EmailChangeModule {}
