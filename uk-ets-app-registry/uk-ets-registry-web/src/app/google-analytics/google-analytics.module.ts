import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { GoogleAnalyticsReducers } from '@google-analytics/reducers';
import { GoogleAnalyticsEffects } from '@google-analytics/effects/google-analytics.effects';
import { GaFactoryService } from '@google-analytics/services/ga-factory.service';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    StoreModule.forFeature(
      GoogleAnalyticsReducers.googleAnalyticsFeatureKey,
      GoogleAnalyticsReducers.reducer
    ),
    EffectsModule.forFeature([GoogleAnalyticsEffects]),
  ],
  providers: [GaFactoryService],
})
export class GoogleAnalyticsModule {}
