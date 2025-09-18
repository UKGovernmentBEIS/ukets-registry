import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { NotificationsWizardPathsModel } from '@notifications/notifications-wizard/model';
import { CancelNotificationRequestContainerComponent } from '@notifications/notifications-wizard/component/cancel-notification-request';
import { NotificationScheduledDateContainerComponent } from '@notifications/notifications-wizard/component/notification-scheduled-date';
import { NotificationRequestGuard } from '@notifications/notifications-wizard/guards';
import { NotificationContentContainerComponent } from '@notifications/notifications-wizard/component/notification-content';
import { NotificationCheckAndSubmitContainerComponent } from '@notifications/notifications-wizard/component/notification-check-and-submit';
import { NotificationRequestSubmittedContainerComponent } from '@notifications/notifications-wizard/component/notification-request-submitted';
import { LoadNotificationWizardContainerComponent } from '@notifications/notifications-wizard/component/select-notification-type';
import { NotificationsEmailUploadFileContainerComponent } from '@notifications/notifications-wizard/component/notifications-email-upload-file/notifications-email-upload-file-container.component';
import { CancelActiveOrPendingNotificationContainerComponent  } from './component/cancel-active-or-pending-notification';
import { CancelNotificationSubmittedContainerComponent } from './component/cancel-notification-submitted';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    children: [
      {
        path: '',
        canActivate: [NotificationRequestGuard],
        component: LoadNotificationWizardContainerComponent,
      },
    ],
  },
  {
    path: NotificationsWizardPathsModel.CANCEL_UPDATE_REQUEST,
    canActivate: [LoginGuard],
    component: CancelNotificationRequestContainerComponent,
  },
  {
    path: NotificationsWizardPathsModel.SCHEDULED_DATE,
    canActivate: [LoginGuard],
    component: NotificationScheduledDateContainerComponent,
  },
  {
    path: NotificationsWizardPathsModel.CONTENT,
    canActivate: [LoginGuard],
    component: NotificationContentContainerComponent,
  },
  {
    path: NotificationsWizardPathsModel.CHECK_AND_SUBMIT,
    canActivate: [LoginGuard],
    component: NotificationCheckAndSubmitContainerComponent,
  },
  {
    path: NotificationsWizardPathsModel.REQUEST_SUBMITTED,
    canActivate: [LoginGuard],
    component: NotificationRequestSubmittedContainerComponent,
  },
  {
    path: NotificationsWizardPathsModel.ADHOC_EMAIL,
    canActivate: [LoginGuard],
    component: NotificationsEmailUploadFileContainerComponent,
  },
  {
    path: NotificationsWizardPathsModel.CANCEL_NOTIFICATION,
    canActivate: [LoginGuard],
    component: CancelActiveOrPendingNotificationContainerComponent,
  },
  {
    path: NotificationsWizardPathsModel.NOTIFICATION_CANCELLED,
    canActivate: [LoginGuard],
    component: CancelNotificationSubmittedContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NotificationsWizardRoutingModule {}
