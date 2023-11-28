import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AddAccountComponent,
  AddAccountContainerComponent,
  CancelPendingActivationComponent,
  CancelPendingActivationContainerComponent,
  CancelUpdateRequestContainerComponent,
  ChangeAccountDescriptionComponent,
  ChangeAccountDescriptionContainerComponent,
  CheckUpdateRequestComponent,
  CheckUpdateRequestContainerComponent,
  ConfirmChangeAccountDescriptionComponent,
  ConfirmChangeAccountDescriptionContainerComponent,
  RemoveAccountComponent,
  RemoveAccountContainerComponent,
  RequestSubmittedContainerComponent,
  SelectTypeComponent,
  SelectTypeContainerComponent,
  SubmitSuccessChangeDescriptionContainerComponent,
} from '@trusted-account-list/components';
import { TrustedAccountListRoutingModule } from '@trusted-account-list/trusted-account-list-routing.module';
import { StoreModule } from '@ngrx/store';
import * as fromTrustedAccountList from './reducers';
import { EffectsModule } from '@ngrx/effects';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import {
  TrustedAccountDescriptionUpdateEffects,
  TrustedAccountListEffects,
  TrustedAccountListNavigationEffects,
} from '@trusted-account-list/effects';
import { TrustedAccountListApiService } from '@trusted-account-list/services';
import { CheckUpdateRequestResolver } from '@trusted-account-list/resolvers';
import { AccountModule } from '@account-management/account/account.module';

@NgModule({
  declarations: [
    SelectTypeComponent,
    SelectTypeContainerComponent,
    AddAccountComponent,
    RemoveAccountComponent,
    CheckUpdateRequestComponent,
    ChangeAccountDescriptionComponent,
    ConfirmChangeAccountDescriptionComponent,
    AddAccountContainerComponent,
    RemoveAccountContainerComponent,
    CheckUpdateRequestContainerComponent,
    RequestSubmittedContainerComponent,
    CancelUpdateRequestContainerComponent,
    ChangeAccountDescriptionContainerComponent,
    ConfirmChangeAccountDescriptionContainerComponent,
    SubmitSuccessChangeDescriptionContainerComponent,
    CancelPendingActivationComponent,
    CancelPendingActivationContainerComponent,
  ],
  imports: [
    CommonModule,
    TrustedAccountListRoutingModule,
    StoreModule.forFeature(
      fromTrustedAccountList.trustedAccountListFeatureKey,
      fromTrustedAccountList.reducer
    ),
    EffectsModule.forFeature([
      TrustedAccountListEffects,
      TrustedAccountListNavigationEffects,
      TrustedAccountDescriptionUpdateEffects,
    ]),
    SharedModule,
    ReactiveFormsModule,
    AccountModule,
  ],
  providers: [TrustedAccountListApiService, CheckUpdateRequestResolver],
})
export class TrustedAccountListModule {}
