import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReconciliationRouteModule } from '@reconciliation-administration/reconciliation-routing.module';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import * as fromReconciliationReducer from '@reconciliation-administration/reducers';
import { ReconciliationEffects } from '@reconciliation-administration/effects/reconciliation.effects';
import { ReconciliationStartContainerComponent } from '@reconciliation-administration/components/reconciliation-start';
import { ReconciliationService } from '@reconciliation-administration/service/reconciliation.service';

@NgModule({
  declarations: [ReconciliationStartContainerComponent],
  providers: [ReconciliationService],
  imports: [
    ReconciliationRouteModule,
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    StoreModule.forFeature(
      fromReconciliationReducer.reconciliationFeatureKey,
      fromReconciliationReducer.reducer
    ),
    EffectsModule.forFeature([ReconciliationEffects]),
  ],
})
export class ReconciliationModule {}
