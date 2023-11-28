import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TimeoutBannerContainerComponent } from '@registry-web/timeout/timeout-banner/timeout-banner-container.component';
import { TimeoutBannerComponent } from '@registry-web/timeout/timeout-banner/timeout-banner.component';
import { StoreModule } from '@ngrx/store';
import * as fromTimeout from './store/timeout.reducer';
import { EffectsModule } from '@ngrx/effects';
import { TimeoutEffects } from '@registry-web/timeout/store/timeout.effects';
import { SecondsToMinutesPipe } from '@registry-web/timeout/pipes/seconds-to-minutes.pipe';
import { TimedOutComponent } from './timed-out/timed-out.component';
import { RouterModule } from '@angular/router';
import { A11yModule } from '@angular/cdk/a11y';

@NgModule({
  declarations: [
    TimeoutBannerContainerComponent,
    TimeoutBannerComponent,
    SecondsToMinutesPipe,
    TimedOutComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    A11yModule,
    StoreModule.forFeature(fromTimeout.timeoutFeatureKey, fromTimeout.reducer),
    EffectsModule.forFeature([TimeoutEffects]),
  ],
  exports: [TimeoutBannerContainerComponent, TimedOutComponent],
})
export class TimeoutModule {}
