import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import {
  BulkActionsEffects,
  bulkActionsFeature,
} from '@shared/task-and-regulator-notice-management/bulk-actions/store';
import {
  BulkActionSuccessComponent,
  BulkActionsButtonGroupComponent,
} from '@shared/task-and-regulator-notice-management/bulk-actions/components';

@NgModule({
  imports: [
    StoreModule.forFeature(bulkActionsFeature),
    EffectsModule.forFeature([BulkActionsEffects]),
    BulkActionsButtonGroupComponent,
    BulkActionSuccessComponent,
  ],
  exports: [BulkActionsButtonGroupComponent, BulkActionSuccessComponent],
})
export class BulkActionsModule {}
