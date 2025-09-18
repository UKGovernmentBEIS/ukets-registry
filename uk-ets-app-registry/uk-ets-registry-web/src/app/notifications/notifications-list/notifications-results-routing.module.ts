import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LoginGuard } from '@shared/guards';
import { NotificationsResultsContainerComponent } from '@notifications/notifications-list/components/notifications-results';
import { NotificationsWizardPathsModel } from '@notifications/notifications-wizard/model';
import { ClearNotificationsDetailsGuard } from '@notifications/notifications-details/guards';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: NotificationsResultsContainerComponent,
    title: 'Notifications',
  },
  {
    path: NotificationsWizardPathsModel.BASE_PATH,
    loadChildren: () => {
      return import('../notifications-wizard/notifications-wizard.module').then(
        (m) => m.NotificationsWizardModule
      );
    },
  },
  {
    path: ':notificationId',
    canDeactivate: [ClearNotificationsDetailsGuard],
    loadChildren() {
      return import(
        '../notifications-details/notification-details.module'
      ).then((m) => m.NotificationDetailsModule);
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NotificationsResultsRoutingModule {}
