import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SortParameters } from '@shared/search/sort/SortParameters';
import {
  NotificationProjection,
  notificationStatusMap,
} from '@notifications/notifications-list/model';
import {
  NotificationsWizardPathsModel,
  NotificationTypeLabels,
} from '@notifications/notifications-wizard/model';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import {
  changePageSize,
  navigateToFirstPageOfResults,
  navigateToLastPageOfResults,
  navigateToNextPageOfResults,
  navigateToPreviousPageOfResults,
} from '@notifications/notifications-list/actions/notifications-list.actions';

@Component({
  selector: 'app-notifications-results',
  templateUrl: './notifications-results.component.html',
})
export class NotificationsResultsComponent {
  @Input() isSeniorAdmin: boolean;
  @Input() pagination: Pagination;
  @Input() sortParameters: SortParameters;
  @Input() results: NotificationProjection[];
  @Output() readonly newNotificationEmitter = new EventEmitter();
  @Output() readonly sort = new EventEmitter<SortParameters>();
  @Output() readonly pageParametersEmitter = new EventEmitter<any>();

  viewNotificationPath = NotificationsWizardPathsModel.BASE_PATH;
  notificationTypeLabels = NotificationTypeLabels;
  notificationStatusMap = notificationStatusMap;
  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];

  goToNewNotification(): void {
    this.newNotificationEmitter.emit();
  }

  onSorting($event: SortParameters) {
    this.sort.emit($event);
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.pageParametersEmitter.emit({
      pageParameters,
      action: navigateToFirstPageOfResults,
    });
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.pageParametersEmitter.emit({
      pageParameters,
      action: navigateToLastPageOfResults,
    });
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.pageParametersEmitter.emit({
      pageParameters,
      action: navigateToNextPageOfResults,
    });
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.pageParametersEmitter.emit({
      pageParameters,
      action: navigateToPreviousPageOfResults,
    });
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.pageParametersEmitter.emit({
      pageParameters,
      action: changePageSize,
    });
  }
}
