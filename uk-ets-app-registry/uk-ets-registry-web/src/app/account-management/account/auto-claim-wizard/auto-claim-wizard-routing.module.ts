import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  CancelAutoClaimContainerComponent,
  ConfirmAutoClaimContainerComponent,
} from '@auto-claim-wizard/components';
import { LoginGuard } from '@shared/guards/login.guard';

export const AUTO_CLAIM_BASE_PATH = 'auto-claim';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: ConfirmAutoClaimContainerComponent,
  },
  {
    path: 'confirm-auto-claim-change',
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: 'cancel-auto-claim',
    canActivate: [LoginGuard],
    component: CancelAutoClaimContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AutoClaimWizardRoutingModule {}
