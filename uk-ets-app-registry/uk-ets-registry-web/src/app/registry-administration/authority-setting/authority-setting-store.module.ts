import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import * as fromAuthoritySetting from '@authority-setting/reducer';
import { EffectsModule } from '@ngrx/effects';
import { AuthoritySettingEffect } from '@authority-setting/effect';
import { AuthoritySettingService } from '@authority-setting/service';

@NgModule({
  imports: [
    StoreModule.forFeature(
      fromAuthoritySetting.authoritySettingFeatureKey,
      fromAuthoritySetting.reducer
    ),
    EffectsModule.forFeature([AuthoritySettingEffect])
  ],
  providers: [AuthoritySettingService]
})
export class AuthoritySettingStoreModule {}
