import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { LoginGuard } from '@registry-web/shared/guards';
import { ReportsModule } from '@registry-web/reports/reports.module';
import {
  BULK_ASSIGN_PATH,
  BULK_CLAIM_PATH,
  BulkActionsModule,
  BulkAssignComponent,
  BulkClaimComponent,
  createBulkAssignErrorMap,
  createBulkClaimErrorMap,
} from '@shared/task-and-regulator-notice-management/bulk-actions';
import { createRegulatorNoticesListErrorMap } from '@regulator-notice-management/list/util/potential-error-map.factory';
import { RegulatorNoticeSearchResolver } from '@regulator-notice-management/list/guards-and-resolvers';
import {
  RegulatorNoticeListEffects,
  regulatorNoticeListFeature,
} from '@regulator-notice-management/list/store';
import { RegulatorNoticeListContainerComponent } from '@regulator-notice-management/list/components';
import { SelectedRegulatorNoticesResolver } from '@regulator-notice-management/list/guards-and-resolvers';

export const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: RegulatorNoticeListContainerComponent,
    resolve: { search: RegulatorNoticeSearchResolver },
    data: { errorMap: createRegulatorNoticesListErrorMap() },
    title: 'Regulator Notices',
  },
  {
    path: BULK_CLAIM_PATH,
    canActivate: [LoginGuard],
    component: BulkClaimComponent,
    resolve: { selectedTasks: SelectedRegulatorNoticesResolver },
    data: { errorMap: createBulkClaimErrorMap('notice') },
  },
  {
    path: BULK_ASSIGN_PATH,
    canActivate: [LoginGuard],
    component: BulkAssignComponent,
    resolve: { selectedTasks: SelectedRegulatorNoticesResolver },
    data: { errorMap: createBulkAssignErrorMap('notice') },
  },
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
    StoreModule.forFeature(regulatorNoticeListFeature),
    EffectsModule.forFeature([RegulatorNoticeListEffects]),
    ReportsModule,
    BulkActionsModule,
  ],
  providers: [RegulatorNoticeSearchResolver, SelectedRegulatorNoticesResolver],
})
export class RegulatorNoticeListModule {}
