import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClearAllocationTableUploadGuard } from '@allocation-table/guards';
import { ClearEmissionsTableUploadGuard } from '@emissions-table/guards';

const routes: Routes = [
  {
    path: 'issue-allowances',
    loadChildren: () =>
      import('./issue-allowances/issue-allowances.module').then(
        (m) => m.IssueAllowancesModule
      ),
  },
  {
    path: 'allocation-table',
    canDeactivate: [ClearAllocationTableUploadGuard],
    loadChildren: () =>
      import('./allocation-table/allocation-table.module').then(
        (m) => m.AllocationTableModule
      ),
  },
  {
    path: 'request-allocation',
    loadChildren: () =>
      import('./request-allocation/request-allocation.module').then(
        (m) => m.RequestAllocationModule
      ),
  },
  {
    path: 'reconciliation',
    loadChildren: () =>
      import('./reconciliation/reconciliation.module').then(
        (m) => m.ReconciliationModule
      ),
  },
  {
    path: 'issuance-allocation-status',
    loadChildren: () =>
      import(
        './issuance-allocation-status/issuance-allocation-status.module'
      ).then((m) => m.IssuanceAllocationStatusModule),
  },
  {
    path: 'emissions-table',
    canDeactivate: [ClearEmissionsTableUploadGuard],
    loadChildren: () =>
      import('./emissions-table/emissions-table.module').then(
        (m) => m.EmissionsTableModule
      ),
  },
  {
    path: 'recalculate-compliance-status',
    loadChildren: () =>
      import(
        './recalculate-compliance-status/recalculate-compliance-status.module'
      ).then((m) => m.RecalculateComplianceStatusModule),
  },
  {
    path: 'view-allocation-job-status',
    loadChildren: () =>
      import('./allocation-job-status/allocation-job-status.module').then(
        (m) => m.AllocationJobStatusModule
      ),
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EtsAdministrationRoutingModule {}
