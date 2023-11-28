import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { OverviewComponent } from './overview/overview.component';
import { PersonalDetailsContainerComponent } from './personal-details/personal-details-container.component';
import { ContactDetailsContainerComponent } from './contact-details/contact-details-container.component';
import { ACCOUNT_HOLDER_CONTACT_ROUTES } from './account-holder-contact.routes';

@NgModule({
  declarations: [
    PersonalDetailsContainerComponent,
    ContactDetailsContainerComponent,
    OverviewComponent
  ],
  imports: [
    RouterModule.forChild(ACCOUNT_HOLDER_CONTACT_ROUTES),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedModule
  ]
})
export class AccountHolderContactModule {}
