import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  cancelRecipientsEmailUpload,
  clearNotificationsRequest,
  clearRecipientsEmailUpload,
  setNotificationDefinition,
  setNotificationsContent,
  setNotificationsInfoSuccess,
  setNotificationsRequest,
  setNotificationsScheduledDate,
  setRequestNotificationType,
  submitEmailRecipientsFileSuccess,
  submitRequestSuccess,
} from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import {
  Notification,
  NotificationDefinition,
  NotificationType,
} from '@notifications/notifications-wizard/model';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';
import { NotificationsFile, UploadStatus } from '@shared/model/file';
import {
  processSelectedFileError,
  uploadRecipientsEmailFileSuccess,
  uploadSelectedFileHasStarted,
  uploadSelectedFileInProgress,
} from '@shared/file/actions/file-upload-api.actions';

export const notificationsWizardFeatureKey = 'notifications-wizard';

export interface NotificationsWizardState {
  notificationId: string;
  notificationType: NotificationType;
  notificationRequest: NotificationRequestEnum;
  notification: Notification;
  notificationNew: Notification;
  submittedNotificationId: string;
  notificationDefinition: NotificationDefinition;
  datesConvertedToLocalTimezone: boolean;
  status: UploadStatus;
  progress: number | null;
  fileHeader: NotificationsFile;
}

export const initialState: NotificationsWizardState = {
  notificationId: null,
  notificationType: null,
  notificationRequest: null,
  notification: null,
  notificationNew: null,
  submittedNotificationId: null,
  notificationDefinition: null,
  datesConvertedToLocalTimezone: false,
  status: UploadStatus.Ready,
  progress: null,
  fileHeader: {
    id: null,
    fileName: null,
    fileSize: null,
  },
};

const notificationsWizardReducer = createReducer(
  initialState,
  mutableOn(
    setNotificationsRequest,
    (state, { notificationRequest, notificationId }) => {
      resetState(state);
      state.notificationRequest = notificationRequest;
      state.notificationId = notificationId;
    }
  ),
  mutableOn(setNotificationsInfoSuccess, (state, { notification }) => {
    state.notification = notification;
    state.notificationNew = notification;
  }),

  mutableOn(setRequestNotificationType, (state, { notificationType }) => {
    if (state.notificationNew) {
      state.notificationNew.type = notificationType;
    } else {
      state.notificationNew = {
        type: notificationType,
        activationDetails: null,
        contentDetails: null,
      };
    }
  }),
  mutableOn(setNotificationDefinition, (state, { notificationDefinition }) => {
    state.notificationDefinition = notificationDefinition;
  }),
  mutableOn(
    setNotificationsScheduledDate,
    (state, { notificationScheduledDate }) => {
      state.notificationNew.activationDetails = notificationScheduledDate;
      state.datesConvertedToLocalTimezone = true;
    }
  ),
  mutableOn(setNotificationsContent, (state, { notificationContent }) => {
    state.notificationNew.contentDetails = notificationContent;
  }),
  mutableOn(submitRequestSuccess, (state, { requestId }) => {
    state.submittedNotificationId = requestId;
  }),
  mutableOn(clearNotificationsRequest, (state) => {
    resetState(state);
  }),

  mutableOn(uploadSelectedFileHasStarted, (state, { status }) => {
    state.progress = 0;
    state.status = status;
  }),
  mutableOn(uploadSelectedFileInProgress, (state, { progress }) => {
    state.progress = progress;
  }),
  mutableOn(uploadRecipientsEmailFileSuccess, (state, { fileHeader }) => {
    state.fileHeader = fileHeader;
    state.progress = 100;
    state.status = UploadStatus.Completed;
  }),
  mutableOn(processSelectedFileError, (state) => {
    state.status = UploadStatus.Failed;
  }),
  mutableOn(cancelRecipientsEmailUpload, (state) => {
    resetState(state);
  }),
  mutableOn(clearRecipientsEmailUpload, (state) => {
    resetState(state);
  }),
  mutableOn(submitEmailRecipientsFileSuccess, (state, { requestId }) => {
    state.status = UploadStatus.Completed;
  })
);

export function reducer(
  state: NotificationsWizardState | undefined,
  action: Action
) {
  return notificationsWizardReducer(state, action);
}

function resetState(state) {
  state.notificationId = initialState.notificationId;
  state.notificationType = initialState.notificationType;
  state.notificationRequest = initialState.notificationRequest;
  state.submittedRequestIdentifier = initialState.submittedNotificationId;
  state.notification = initialState.notification;
  state.notificationNew = initialState.notificationNew;
  state.notificationDefinition = initialState.notificationDefinition;
  state.datesConvertedToLocalTimezone =
    initialState.datesConvertedToLocalTimezone;
  state.fileHeader = initialState.fileHeader;
}
