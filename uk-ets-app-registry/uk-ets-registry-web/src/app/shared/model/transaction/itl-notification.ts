//TODO maybe extract this to a common module in order to merge ot with notice.ts from itl-notices
export interface ItlNotification {
  notificationIdentifier: number;
  quantity: number;
  commitPeriod: number;
  targetDate: Date;
  projectNumber: string;
  type: string;
}
