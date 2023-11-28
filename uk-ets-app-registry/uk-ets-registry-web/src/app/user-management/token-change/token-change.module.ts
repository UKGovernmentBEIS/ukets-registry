import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TokenChangeRoutingModule } from '@user-management/token-change/token-change-routing.module';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { TokeChangeEffects } from '@user-management/token-change/effect';
import * as fromTokenChange from './reducer';
import { AuthApiService } from '../../auth/auth-api.service';
import { TokenChangeService } from '@user-management/token-change/service/token-change.service';
import {
  TokenChangeClickedEmailComponent,
  TokenChangeClickedEmailContainerComponent,
  TokenChangeEnterReasonComponent,
  TokenChangeEnterReasonContainerComponent,
  TokenChangeEnterTokenComponent,
  TokenChangeEnterTokenContainerComponent,
  TokenChangeLinkExpiredComponent,
  TokenChangeLinkExpiredContainerComponent,
  TokenChangeVerificationComponent,
  TokenChangeVerificationContainerComponent
} from '@user-management/token-change/component';

@NgModule({
  declarations: [
    TokenChangeEnterReasonComponent,
    TokenChangeEnterTokenComponent,
    TokenChangeVerificationComponent,
    TokenChangeEnterTokenContainerComponent,
    TokenChangeVerificationContainerComponent,
    TokenChangeEnterReasonContainerComponent,
    TokenChangeLinkExpiredComponent,
    TokenChangeLinkExpiredContainerComponent,
    TokenChangeClickedEmailComponent,
    TokenChangeClickedEmailContainerComponent
  ],
  imports: [
    CommonModule,
    TokenChangeRoutingModule,
    ReactiveFormsModule,
    SharedModule,
    StoreModule.forFeature(
      fromTokenChange.tokenChangeReducerFeatureKey,
      fromTokenChange.reducer
    ),
    EffectsModule.forFeature([TokeChangeEffects])
  ],
  providers: [AuthApiService, TokenChangeService]
})
export class TokenChangeModule {}
