import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  NotificationsWizardPathsModel,
  NotificationTypeLabels,
} from '@notifications/notifications-wizard/model';
import {
  hideCriteria,
  navigateTo,
  searchNotifications,
  showCriteria,
  sortResults,
} from '@notifications/notifications-list/actions/notifications-list.actions';
import { canGoBack, clearErrors } from '@shared/shared.action';
import {
  selectCriteria,
  selectHideCriteria,
  selectPagination,
  selectResults,
  selectSortParameters,
} from '@notifications/notifications-list/reducers/notifications-list.selector';

import { SortParameters } from '@shared/search/sort/SortParameters';
import { ErrorDetail } from '@shared/error-summary';
import { Observable } from 'rxjs';
import {
  FiltersDescriptor,
  NotificationProjection,
  NotificationSearchCriteria,
  SearchActionPayload,
  SearchHelper,
} from '@notifications/notifications-list/model';
import { Pagination } from '@shared/search/paginator';
import { isSeniorAdmin } from '@registry-web/auth/auth.selector';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-notifications-results-container',
  templateUrl: './notifications-results-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationsResultsContainerComponent implements OnInit {
  pagination$: Observable<Pagination>;
  results$: Observable<NotificationProjection[]>;
  sortParameters: SortParameters = {
    sortField: 'scheduledDate',
    sortDirection: 'DESC',
  };
  sortParameters$: Observable<SortParameters>;
  isSeniorAdmin$: Observable<boolean>;
  hideCriteria$: Observable<boolean>;
  potentialErrors: Map<any, ErrorDetail>;
  pageSize = 10;
  loadPageParametersFromState = true;

  filtersDescriptor: FiltersDescriptor;
  private criteria: NotificationSearchCriteria;
  storedCriteria$: Observable<NotificationSearchCriteria>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));

    this.sortParameters$ = this.store.select(selectSortParameters);
    this.results$ = this.store.select(selectResults);
    this.pagination$ = this.store.select(selectPagination);
    this.isSeniorAdmin$ = this.store.select(isSeniorAdmin);
    this.hideCriteria$ = this.store.select(selectHideCriteria);
    this.storedCriteria$ = this.store.select(selectCriteria);

    this.filtersDescriptor = {
      typeOptions: Object.entries(NotificationTypeLabels).map(
        ([key, value]: [string, { label: string; description?: string }]) => ({
          label: value.label,
          value: key,
        })
      ),
    };
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.store.dispatch(clearErrors());
    const payload: SearchActionPayload = {
      criteria: this.criteria,
      pageParameters: {
        page: 0,
        pageSize: this.pageSize,
      },
      sortParameters: sortParameters,
      potentialErrors: this.potentialErrors,
      loadPageParametersFromState: true,
    };
    this.store.dispatch(sortResults(payload));
  }

  searchNotifications(value: SearchHelper) {
    this.store.dispatch(clearErrors());
    this.pageSize = value.pageParameters.pageSize;
    const payload: SearchActionPayload = {
      criteria: this.criteria,
      pageParameters: value.pageParameters,
      sortParameters: this.sortParameters,
      potentialErrors: this.potentialErrors,
      loadPageParametersFromState: value.loadPageParametersFromState,
    };
    this.store.dispatch(value.action(payload));
  }

  goToNewNotification(): void {
    this.store.dispatch(
      navigateTo({
        route: `/notifications/${NotificationsWizardPathsModel.BASE_PATH}`,
        extras: {
          skipLocationChange: false,
          queryParams: {},
        },
      })
    );
  }

  hideShowCriteria($event: boolean) {
    $event
      ? this.store.dispatch(hideCriteria())
      : this.store.dispatch(showCriteria());
  }

  onSearchNotifications(
    criteria: NotificationSearchCriteria,
    sortParameters: SortParameters
  ) {
    this.sortParameters = sortParameters;
    this.criteria = criteria;
    this.searchNotifications({
      action: searchNotifications,
      criteria: criteria,
      pageParameters: {
        page: 0,
        pageSize: this.pageSize,
      },
      loadPageParametersFromState: this.loadPageParametersFromState,
    });
  }

  onSubmitClick() {
    this.loadPageParametersFromState = false;
  }
}
