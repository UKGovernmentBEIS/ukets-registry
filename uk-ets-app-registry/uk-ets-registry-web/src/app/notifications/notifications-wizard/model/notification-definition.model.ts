import { NotificationType } from '@notifications/notifications-wizard/model/notification.type';

export interface NotificationDefinition {
  type?: NotificationType;
  shortText: string;
  longText: string;
  tentativeRecipients?: number;
}
