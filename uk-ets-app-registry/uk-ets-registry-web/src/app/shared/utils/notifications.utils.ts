import { TimezoneUtils } from '@shared/utils/timezone.utils';
import { Notification } from '@notifications/notifications-wizard/model';

export class NotificationsUtils {
  static convertNotificationTimeZone(
    notification: Notification,
    convertToUTC: boolean
  ) {
    const notificationDetails = notification ? { ...notification } : null;
    if (notification?.activationDetails) {
      const activationDetails = notificationDetails.activationDetails;
      const scheduledDateTime =
        activationDetails.scheduledDate + ' ' + activationDetails.scheduledTime;
      const scheduledDateTimeObj = TimezoneUtils.calculateTimeZone(
        scheduledDateTime,
        convertToUTC
      );

      let expirationDateTimeObj = null;
      if (activationDetails.expirationDate) {
        let expirationDateTime;
        if (!activationDetails?.expirationTime) {
          expirationDateTime = activationDetails.expirationDate + ' 12:00:00';
        } else {
          expirationDateTime =
            activationDetails.expirationDate +
            ' ' +
            activationDetails.expirationTime;
        }
        expirationDateTimeObj = TimezoneUtils.calculateTimeZone(
          expirationDateTime,
          convertToUTC
        );
      }

      notificationDetails.activationDetails = {
        ...activationDetails,
        scheduledDate: scheduledDateTimeObj.date,
        scheduledTime: scheduledDateTimeObj.time,
        expirationDate: expirationDateTimeObj
          ? expirationDateTimeObj.date
          : activationDetails?.expirationDate,
        expirationTime: expirationDateTimeObj
          ? expirationDateTimeObj.time
          : activationDetails?.expirationTime,
      };
    }
    return notificationDetails;
  }
}
