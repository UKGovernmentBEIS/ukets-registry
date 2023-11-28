import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { GenerateLogsEffects } from '@generate-logs/effects/generate-logs.effects';
import { GenerateLogsReducer } from '@generate-logs/reducers';
import { UILogsApiService } from '@generate-logs/services/ui-logs-api.service';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(
      GenerateLogsReducer.generateLogsFeatureKey,
      GenerateLogsReducer.reducer
    ),
    EffectsModule.forFeature([GenerateLogsEffects]),
  ],
  providers: [UILogsApiService],
})
export class GenerateLogsModule {}
