import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  MessageDetailsContainerComponent,
  MessageFormContainerComponent,
  MessageListContainerComponent,
  SendMessageConfirmationComponent,
} from '@kp-administration/itl-messages/components';
import { MessageHeaderGuard } from '@kp-administration/itl-messages/guards';
import {
  NoticeDetailsComponent,
  NoticeDetailsContainerComponent,
  NoticeDetailsListComponent,
  NoticeListContainerComponent,
} from '@kp-administration/itl-notices/components';
import { NoticeDetailsGuard } from '@kp-administration/itl-notices/guards';
import { ReconciliationAdministrationContainerComponent } from '@kp-administration/reconciliation';

const routes: Routes = [
  {
    path: 'issuekpunits',
    loadChildren: () =>
      import('./issue-kp-units/issue-kp-units.module').then(
        (m) => m.IssueKpUnitsModule
      ),
  },
  {
    path: 'itl-message-list',
    component: MessageListContainerComponent,
  },
  {
    path: 'itl-message-details/:messageId',
    canActivate: [LoginGuard, MessageHeaderGuard],
    canDeactivate: [MessageHeaderGuard],
    component: MessageDetailsContainerComponent,
  },
  {
    path: 'send-itl-message',
    children: [
      {
        path: '',
        canActivate: [LoginGuard],
        component: MessageFormContainerComponent,
      },
      {
        path: 'send-message-success',
        canActivate: [LoginGuard],
        component: SendMessageConfirmationComponent,
      },
    ],
  },
  {
    path: 'itl-notices',
    canActivate: [LoginGuard],
    component: NoticeListContainerComponent,
  },
  {
    path: 'itl-notices/:id',
    canActivate: [LoginGuard],
    component: NoticeDetailsContainerComponent,
    children: [
      {
        path: '',
        component: NoticeDetailsListComponent,
      },
      {
        path: ':typeId',
        canActivate: [NoticeDetailsGuard],
        component: NoticeDetailsComponent,
      },
    ],
  },
  {
    path: 'itl-reconciliation',
    canActivate: [LoginGuard],
    component: ReconciliationAdministrationContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class KpAdministrationRoutingModule {}
