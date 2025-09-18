import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { NotificationDetailsContainerComponent } from '@notifications/notifications-details/components/notification-details';
import { NotificationDetailsGuard } from '@notifications/notifications-details/guards/notification-details-guard';
import { NotificationsWizardPathsModel } from '@notifications/notifications-wizard/model';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard, NotificationDetailsGuard],
    component: NotificationDetailsContainerComponent,
  },
  {
    path: NotificationsWizardPathsModel.BASE_PATH,
    loadChildren() {
      return import('../notifications-wizard/notifications-wizard.module').then(
        (m) => m.NotificationsWizardModule
      );
    },
  },
  {
    path: NotificationsWizardPathsModel.CLONE,
    loadChildren() {
      return import('../notifications-wizard/notifications-wizard.module').then(
        (m) => m.NotificationsWizardModule
      );
    },
  },
  {
    path: NotificationsWizardPathsModel.CANCEL_NOTIFICATION,
    loadChildren() {
      return import('../notifications-wizard/notifications-wizard.module').then(
        (m) => m.NotificationsWizardModule
      );
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NotificationDetailsRoutingModule {}
