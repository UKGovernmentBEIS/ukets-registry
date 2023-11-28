import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OverviewComponent } from './overview/overview.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { RouterModule } from '@angular/router';
import { ACCOUNT_DETAILS_ROUTES } from './account-details.routes';
import { AccountDetailsContainerComponent } from './account-details/account-details-container.component';
import { BillingDetailsContainerComponent } from './billing-details/billing-details-container.component';

@NgModule({
  declarations: [
    AccountDetailsContainerComponent,
    BillingDetailsContainerComponent,
    OverviewComponent,
  ],
  imports: [
    RouterModule.forChild(ACCOUNT_DETAILS_ROUTES),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedModule,
  ],
})
export class AccountDetailsModule {}
