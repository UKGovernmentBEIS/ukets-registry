export enum NotificationType {
  EMISSIONS_MISSING_FOR_OHA = 'EMISSIONS_MISSING_FOR_OHA',
  SURRENDER_DEFICIT_FOR_OHA = 'SURRENDER_DEFICIT_FOR_OHA',
  EMISSIONS_MISSING_FOR_AOHA = 'EMISSIONS_MISSING_FOR_AOHA',
  SURRENDER_DEFICIT_FOR_AOHA = 'SURRENDER_DEFICIT_FOR_AOHA',
  YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS = 'YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS',
  AD_HOC = 'AD_HOC',
  AD_HOC_EMAIL = 'AD_HOC_EMAIL',
  USER_INACTIVITY = 'USER_INACTIVITY',
}

export const NotificationTypeLabels: Record<
  NotificationType,
  { label: string; description?: string }
> = {
  EMISSIONS_MISSING_FOR_OHA: {
    label: 'Emissions missing for OHA accounts',
    description:
      'A reminder notification will be sent by email to the active ARs of the OHA accounts that are in dynamic surrender status C. ' +
      'Notifications will not be sent for accounts with a status of CLOSED, PENDING, SUSPENDED FULLY or PARTIALLY.',
  },
  SURRENDER_DEFICIT_FOR_OHA: {
    label: 'Surrender deficit for OHA accounts',
    description:
      'A reminder notification will be sent by email to the active ARs of the OHA accounts that are in dynamic surrender status B and C. ' +
      'Notifications will not be sent for accounts with a status of CLOSED, PENDING, SUSPENDED FULLY or PARTIALLY.',
  },
  EMISSIONS_MISSING_FOR_AOHA: {
    label: 'Emissions missing for AOHA accounts',
    description:
      'A reminder notification will be sent by email to the active ARs of the AOHA accounts that are in dynamic surrender status C. ' +
      'Notifications will not be sent for accounts with a status of CLOSED, PENDING, SUSPENDED FULLY or PARTIALLY.',
  },
  SURRENDER_DEFICIT_FOR_AOHA: {
    label: 'Surrender deficit for AOHA accounts',
    description:
      'A reminder notification will be sent by email to the active ARs of the AOHA accounts that are in dynamic surrender status B and C. ' +
      'Notifications will not be sent for accounts with a status of CLOSED, PENDING, SUSPENDED FULLY or PARTIALLY.',
  },
  YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS: {
    label:
      'Yearly introduction to OHA/AOHA accounts with reporting obligations',
    description:
      'A yearly introduction will be sent by email to the active ARs of OHA and AOHA accounts that are in dynamic surrender status B and C. ' +
      'Notifications will not be sent for accounts with a status of CLOSED, PENDING, SUSPENDED FULLY or PARTIALLY.',
  },
  AD_HOC: {
    label: 'Ad-hoc dashboard notification',
    description:
      'These notifications will be shown in the Registry dashboard to all users.',
  },
  USER_INACTIVITY: {
    label: 'User inactivity',
    description:
      "A reminder notification will be sent by email to the ARs who haven't logged in for the last 6 months. Notifications will not be sent for deactivated or suspended users.",
  },
  AD_HOC_EMAIL: {
    label: 'Ad-hoc Email Notification',
    description:
      'An email notification will be sent to a selected list of ARs.',
  },
};
