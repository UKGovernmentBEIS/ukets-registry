import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
// eslint-disable-next-line max-len
import { SelectAccountStatusActionContainerComponent } from './components/select-account-status-action/select-account-status-action-container.component';
// eslint-disable-next-line max-len
import { ConfirmAccountStatusActionContainerComponent } from './components/confirm-account-status-action/confirm-account-status-action-container.component';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    component: SelectAccountStatusActionContainerComponent,
  },
  {
    path: 'confirm',
    canLoad: [LoginGuard],
    component: ConfirmAccountStatusActionContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountStatusRoutingModule {}
