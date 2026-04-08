import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  BulkClaimAccountInfoContainerComponent,
  BulkClaimAccountSubmittedContainerComponent,
  CancelBulkClaimAccountContainerComponent,
  ConfirmBulkClaimAccountContainerComponent,
} from '@bulk-claim-account/components';
import { LoginGuard } from '@shared/guards';
import { clearBulkClaimAccountGuard } from '@bulk-claim-account/guards';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: BulkClaimAccountInfoContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: false,
    },
  },
  {
    path: 'check-request-and-submit',
    canActivate: [LoginGuard],
    component: ConfirmBulkClaimAccountContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: true,
    },
  },
  {
    path: 'request-submitted',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    canDeactivate: [clearBulkClaimAccountGuard],
    component: BulkClaimAccountSubmittedContainerComponent,
  },
  {
    path: 'cancel',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    canDeactivate: [clearBulkClaimAccountGuard],
    component: CancelBulkClaimAccountContainerComponent,
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
export class BulkClaimAccountRoutingModule {}
