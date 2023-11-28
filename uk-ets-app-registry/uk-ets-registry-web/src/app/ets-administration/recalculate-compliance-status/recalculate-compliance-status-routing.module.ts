import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { RecalculateStartContainerComponent } from '@recalculate-compliance-status/components/recalculate-start';
import { CheckRequestAndSubmitContainerComponent } from '@recalculate-compliance-status/components/check-request-and-submit';
import { ClearRecalculateComplianceStatusGuard } from '@recalculate-compliance-status/guards';

export const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    canDeactivate: [ClearRecalculateComplianceStatusGuard],
    component: RecalculateStartContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: false,
    },
  },
  {
    path: 'confirm',
    canActivate: [LoginGuard],
    component: CheckRequestAndSubmitContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: false,
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RecalculateComplianceStatusRoutingModule {}
