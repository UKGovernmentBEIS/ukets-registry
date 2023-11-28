import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import * as fromEmailChange from '@email-change/reducer';
import { EffectsModule } from '@ngrx/effects';
import {
  EmailChangeEffect,
  EmailChangeNavigationEffect
} from '@email-change/effect';
import { RequestEmailChangeService } from '@email-change/service/request-email-change.service';
import { AuthApiService } from '../../auth/auth-api.service';

@NgModule({
  imports: [
    StoreModule.forFeature(
      fromEmailChange.emailChangeFeatureKey,
      fromEmailChange.reducer
    ),
    EffectsModule.forFeature([EmailChangeNavigationEffect, EmailChangeEffect])
  ],
  providers: [RequestEmailChangeService, AuthApiService]
})
export class EmailChangeStoreModule {}
