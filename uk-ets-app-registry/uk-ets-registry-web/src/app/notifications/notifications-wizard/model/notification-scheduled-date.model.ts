export interface NotificationScheduledDate {
  scheduledDate: string;
  scheduledTime: string;
  expirationDate: string;
  // For recurrence section (all notification types except ad-hoc)
  hasRecurrence: boolean;
  recurrenceDays: number;
  // For expiration section (only ad-hoc notification)
  hasExpirationDateSection: boolean;
  expirationTime: string;
  scheduledTimeNow: boolean;
}
