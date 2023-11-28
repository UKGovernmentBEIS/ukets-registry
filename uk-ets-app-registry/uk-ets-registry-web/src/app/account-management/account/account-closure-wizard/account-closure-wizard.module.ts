import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import {
  AccountClosureCommentComponent,
  AccountClosureCommentContainerComponent,
} from '@account-management/account/account-closure-wizard/components/account-closure-comment';
import { StoreModule } from '@ngrx/store';
import * as fromAccountClosure from './reducers';
import { AccountClosureWizardRoutingModule } from '@account-management/account/account-closure-wizard/account-closure-wizard-routing.module';
import {
  CheckClosureDetailsComponent,
  CheckClosureDetailsContainerComponent,
} from '@account-management/account/account-closure-wizard/components/check-closure-details';
import { CancelClosureRequestContainerComponent } from '@account-management/account/account-closure-wizard/components/cancel-closure-request';
import {
  ClosureRequestSubmittedComponent,
  ClosureRequestSubmittedContainerComponent,
} from '@account-management/account/account-closure-wizard/components/request-submitted';
import { EffectsModule } from '@ngrx/effects';
import { AccountClosureEffects } from '@account-management/account/account-closure-wizard/effects';

@NgModule({
  declarations: [
    AccountClosureCommentContainerComponent,
    AccountClosureCommentComponent,
    CheckClosureDetailsContainerComponent,
    CheckClosureDetailsComponent,
    CancelClosureRequestContainerComponent,
    ClosureRequestSubmittedContainerComponent,
    ClosureRequestSubmittedComponent,
  ],

  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    AccountClosureWizardRoutingModule,
    StoreModule.forFeature(
      fromAccountClosure.accountClosureFeatureKey,
      fromAccountClosure.accountClosureWizardReducer
    ),
    EffectsModule.forFeature([AccountClosureEffects]),
  ],
})
export class AccountClosureWizardModule {}
