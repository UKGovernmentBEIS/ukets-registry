import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ACCOUNT_OPENING_ROUTES } from './account-opening.routes';
import { InfoComponent } from './info/info.component';
import { AccountTypeComponent } from './account-type/account-type.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import * as fromAccountOpening from './account-opening.reducer';
import { SharedModule } from '@shared/shared.module';
import { MainWizardComponent } from './main-wizard/main-wizard.component';
import { CheckAnswersComponent } from './check-answers/check-answers.component';
import { EffectsModule } from '@ngrx/effects';
import { AccountOpeningEffects } from './account-opening.effect';
import { ConfirmationComponent } from './confirmation/confirmation.component';
import { AccountTypeContainerComponent } from './account-type/account-type-container.component';
import { AccountHolderContactsDetailsComponent } from './account-holder-contacts-details/account-holder-contacts-details.component';
import { AccountHolderContactSummaryComponent } from './account-holder-contact-summary/account-holder-contact-summary.component';
import { TransactionRulesLinkComponent } from './main-wizard/transaction-rules-link.component';
import { AboutPrimaryContactComponent } from './main-wizard/about-primary-contact/about-primary-contact.component';
import { OperatorModule } from '@account-opening/operator/operator.module';

@NgModule({
  declarations: [
    InfoComponent,
    AccountTypeComponent,
    AccountTypeContainerComponent,
    MainWizardComponent,
    CheckAnswersComponent,
    ConfirmationComponent,
    AccountHolderContactsDetailsComponent,
    AccountHolderContactSummaryComponent,
    TransactionRulesLinkComponent,
    AboutPrimaryContactComponent,
  ],
  imports: [
    RouterModule.forChild(ACCOUNT_OPENING_ROUTES),
    ReactiveFormsModule,
    FormsModule,
    CommonModule,
    SharedModule,
    StoreModule.forFeature(
      fromAccountOpening.accountOpeningFeatureKey,
      fromAccountOpening.reducer
    ),
    EffectsModule.forFeature([AccountOpeningEffects]),
    OperatorModule,
  ],
})
export class AccountOpeningModule {}
