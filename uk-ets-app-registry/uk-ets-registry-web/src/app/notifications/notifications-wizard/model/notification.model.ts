import { NotificationContent } from '@notifications/notifications-wizard/model/notification-content.model';
import { NotificationScheduledDate } from '@notifications/notifications-wizard/model/notification-scheduled-date.model';
import { NotificationType } from '@notifications/notifications-wizard/model/notification.type';
import { NotificationStatus } from '@notifications/notifications-list/model';

export interface Notification {
  type: NotificationType;
  activationDetails: NotificationScheduledDate;
  contentDetails: NotificationContent;
  lastUpdated?: Date;
  updatedBy?: string;
  status?: NotificationStatus;
  tentativeRecipients?: number;
  uploadedFileId?: number;
}
