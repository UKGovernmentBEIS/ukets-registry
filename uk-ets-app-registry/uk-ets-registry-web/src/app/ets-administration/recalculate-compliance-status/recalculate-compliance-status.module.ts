import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RecalculateComplianceStatusRoutingModule } from '@recalculate-compliance-status/recalculate-compliance-status-routing.module';
import { RecalculateStartComponent } from '@recalculate-compliance-status/components/recalculate-start/recalculate-start.component';
import { RecalculateStartContainerComponent } from '@recalculate-compliance-status/components/recalculate-start/recalculate-start-container.component';
import { CheckRequestAndSubmitComponent } from '@recalculate-compliance-status/components/check-request-and-submit/check-request-and-submit.component';
import { CheckRequestAndSubmitContainerComponent } from '@recalculate-compliance-status/components/check-request-and-submit/check-request-and-submit-container.component';
import * as fromRecalculateComplianceStatusReducer from '@recalculate-compliance-status/store/reducers';
import { RecalculateComplianceStatusEffects } from '@recalculate-compliance-status/store/effects';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { SharedModule } from '@shared/shared.module';

@NgModule({
  declarations: [
    RecalculateStartContainerComponent,
    CheckRequestAndSubmitComponent,
    CheckRequestAndSubmitContainerComponent,
    RecalculateStartComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    RecalculateComplianceStatusRoutingModule,
    StoreModule.forFeature(
      fromRecalculateComplianceStatusReducer.recalculateComplianceStatusReducerFeatureKey,
      fromRecalculateComplianceStatusReducer.reducer
    ),
    EffectsModule.forFeature([RecalculateComplianceStatusEffects]),
  ],
})
export class RecalculateComplianceStatusModule {}
