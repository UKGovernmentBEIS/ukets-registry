import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Data, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { SortService } from '@shared/search/sort/sort.service';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { allocationYearOptions } from '@shared/shared.selector';
import {
  canGoBack,
  clearErrors,
  errors,
  loadRequestAllocationData,
} from '@shared/shared.action';
import {
  BULK_ASSIGN_PATH,
  BULK_CLAIM_PATH,
  BulkActions,
} from '@shared/task-and-regulator-notice-management/bulk-actions';
import {
  ListMode,
  SelectionChange,
} from '@shared/task-and-regulator-notice-management/model';

import { selectIsReportSuccess } from '@reports/selectors';
import { TaskDetailsActions } from '@task-management/task-details/actions';
import {
  AccountType,
  SearchActionPayload,
  Task,
  TaskSearchCriteria,
  TaskType,
} from '@shared/task-and-regulator-notice-management/model';
import * as TaskListSelectors from '../store/task-list.selector';
import * as TaskListActions from '../store/task-list.actions';
import { TASK_LIST_PATH } from '@task-management/task-list/task-list.const';

@Component({
  selector: 'app-task-search',
  templateUrl: './task-search-container.component.html',
  providers: [SortService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskSearchContainerComponent implements OnInit {
  paginationSelector = TaskListSelectors.selectPagination;
  page: number;
  pageSize = 10;
  criteria: TaskSearchCriteria;
  sortParameters: SortParameters = {
    sortField: 'createdOn',
    sortDirection: 'DESC',
  };
  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  loadPageParametersFromState = true;

  pagination$: Observable<Pagination>;
  results$: Observable<Task[]>;
  hideCriteria$: Observable<boolean>;
  resultsLoaded$: Observable<boolean>;
  storedCriteria$: Observable<TaskSearchCriteria>;
  selectedTasks$: Observable<Task[]>;
  taskTypeOptions$: Observable<TaskType[]>;
  taskTypeOptionsAll$: Observable<TaskType[]>;
  allocationYearOptions$: Observable<Option[]>;
  accountTypeOptions$: Observable<AccountType[]>;

  disableClaimAssignButtons = false;
  potentialErrors: Map<any, ErrorDetail>;
  nonAdminTaskTypeOptions: TaskType[];
  isReportSuccess$: Observable<boolean>;
  showAdvancedSearch$: Observable<boolean>;
  sortParameters$: Observable<SortParameters>;

  constructor(
    private route: ActivatedRoute,
    private store: Store,
    private router: Router
  ) {}

  ngOnInit() {
    this.store.dispatch(loadRequestAllocationData());
    this.store.dispatch(
      BulkActions.SET_CONFIG_VALUES({
        itemTypeLabel: 'task',
        listPath: TASK_LIST_PATH,
      })
    );

    this.pagination$ = this.store.select(TaskListSelectors.selectPagination);
    this.results$ = this.store.select(TaskListSelectors.selectResults);
    this.hideCriteria$ = this.store.select(
      TaskListSelectors.selectHideCriteria
    );
    this.showAdvancedSearch$ = this.store.select(
      TaskListSelectors.selectShowAdvancedSearch
    );
    this.resultsLoaded$ = this.store.select(
      TaskListSelectors.selectResultsLoaded
    );
    this.storedCriteria$ = this.store.select(TaskListSelectors.selectCriteria);
    this.selectedTasks$ = this.store.select(TaskListSelectors.selectedTasks);

    this.sortParameters$ = this.store.select(
      TaskListSelectors.selectSortParameters
    );

    this.route.data.subscribe((data: Data) => {
      this.potentialErrors = data['errorMap'];
    });

    this.taskTypeOptions$ = this.store.select(
      TaskListSelectors.taskTypeOptions
    );

    this.taskTypeOptionsAll$ = this.store.select(
      TaskListSelectors.taskTypeOptionsAll
    );

    this.accountTypeOptions$ = this.store.select(
      TaskListSelectors.accountTypeOptions
    );
    this.allocationYearOptions$ = this.store.select(allocationYearOptions);
    this.isReportSuccess$ = this.store.select(selectIsReportSuccess);
  }

  onSubmitClick() {
    this.loadPageParametersFromState = false;
  }

  searchTasks(
    action,
    pageParameters: PageParameters,
    isReport?: boolean,
    loadPageParametersFromState?: boolean
  ) {
    this.store.dispatch(clearErrors());
    const payload: SearchActionPayload = {
      criteria: this.criteria,
      pageParameters,
      sortParameters: this.sortParameters,
      potentialErrors: this.potentialErrors,
      isReport,
      loadPageParametersFromState,
    };
    this.store.dispatch(action(payload));
  }

  onSearchTasks(
    criteria: TaskSearchCriteria,
    sortParameters: SortParameters,
    isReport?: boolean
  ) {
    this.store.dispatch(TaskListActions.clearTasksSelection());
    this.sortParameters = sortParameters;
    this.criteria = criteria;
    this.searchTasks(
      TaskListActions.searchTasks,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      isReport,
      this.loadPageParametersFromState
    );
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.searchTasks(
      TaskListActions.sortResults,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      false,
      true
    );
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.searchTasks(
      TaskListActions.navigateToFirstPageOfResults,
      pageParameters
    );
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.searchTasks(
      TaskListActions.navigateToLastPageOfResults,
      pageParameters
    );
  }

  goToPageOfResults(pageParameters: PageParameters) {
    this.searchTasks(TaskListActions.navigateToPageOfResults, pageParameters);
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.searchTasks(
      TaskListActions.navigateToNextPageOfResults,
      pageParameters
    );
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.searchTasks(
      TaskListActions.navigateToPreviousPageOfResults,
      pageParameters
    );
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.searchTasks(TaskListActions.changePageSize, pageParameters);
  }

  hideShowCriteria(isVisible: boolean) {
    this.store.dispatch(
      isVisible
        ? TaskListActions.hideCriteria()
        : TaskListActions.showCriteria()
    );
  }

  toggleAdvancedSearch(event: boolean) {
    this.store.dispatch(
      TaskListActions.showAdvancedSearch({ isVisible: event })
    );
  }

  onSelectedTasksChanged($event: SelectionChange<Task>) {
    this.store.dispatch(
      TaskListActions.updateSelectedTasks({
        added: $event.added,
        removed: $event.removed,
      })
    );
  }

  onClaim() {
    this.dispatchcanGoBackToStoredStateAction();
    this.router.navigate([BULK_CLAIM_PATH], { relativeTo: this.route });
  }

  onAssign() {
    this.dispatchcanGoBackToStoredStateAction();
    this.router.navigate([BULK_ASSIGN_PATH], { relativeTo: this.route });
  }

  private dispatchcanGoBackToStoredStateAction() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/${TASK_LIST_PATH}`,
        extras: {
          queryParams: { mode: ListMode.CACHE },
        },
      })
    );
  }

  onOpenTaskDetail($event: string) {
    this.store.dispatch(
      TaskDetailsActions.loadTaskFromList({ taskId: $event })
    );
  }

  requestReport(criteria: TaskSearchCriteria) {
    this.onSearchTasks(criteria, this.sortParameters, true);
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = { errors: value };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
