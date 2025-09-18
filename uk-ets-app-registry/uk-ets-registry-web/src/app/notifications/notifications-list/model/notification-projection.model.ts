import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { ErrorDetail } from '@shared/error-summary';
import { Status } from '@shared/model/status';
import { NotificationType } from '@notifications/notifications-wizard/model';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';

export interface NotificationProjection {
  notificationId: string;
  shortText: string;
  type: NotificationType;
  scheduledDate: string;
  runEveryXDays: string;
  expirationDate: string;
  status: NotificationStatus;
}

export interface SearchActionPayload {
  criteria: NotificationSearchCriteria;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  potentialErrors: Map<any, ErrorDetail>;
  loadPageParametersFromState?: boolean;
}

export interface SearchHelper {
  criteria: NotificationSearchCriteria;
  pageParameters: PageParameters;
  action: Function;
  loadPageParametersFromState?: boolean;
}

export type NotificationStatus = 'ACTIVE' | 'EXPIRED' | 'PENDING' | 'CANCELLED';

export const notificationStatusMap: Record<NotificationStatus, Status> = {
  ACTIVE: { color: 'blue', label: 'Active' },
  PENDING: { color: 'yellow', label: 'Pending' },
  EXPIRED: { color: 'grey', label: 'Expired' },
  CANCELLED: { color: 'red', label: 'Cancelled' },
};

export interface NotificationSearchCriteria {
  type: NotificationType;
}

export interface FiltersDescriptor {
  typeOptions: Option[];
}
