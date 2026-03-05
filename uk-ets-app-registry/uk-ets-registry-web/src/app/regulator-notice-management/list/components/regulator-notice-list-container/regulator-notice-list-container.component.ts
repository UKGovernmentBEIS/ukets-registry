import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
} from '@angular/core';
import { ActivatedRoute, Data, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { PageParameters } from '@shared/search/paginator';
import { SharedModule } from '@registry-web/shared/shared.module';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import {
  canGoBack,
  clearErrors,
  errors,
  loadRequestAllocationData,
} from '@shared/shared.action';
import { SortService } from '@shared/search/sort/sort.service';
import { SortParameters } from '@shared/search/sort/SortParameters';
import {
  ListMode,
  RegulatorNoticeSearchCriteria,
  SelectionChange,
  RegulatorNoticeTask,
  SearchRegulatorNoticesActionPayload,
} from '@shared/task-and-regulator-notice-management/model';
import { selectIsReportSuccess } from '@reports/selectors';
import {
  BULK_ASSIGN_PATH,
  BULK_CLAIM_PATH,
  BulkActionSuccessComponent,
  BulkActions,
  BulkActionsButtonGroupComponent,
} from '@shared/task-and-regulator-notice-management/bulk-actions';
import {
  RegulatorNoticeListActions,
  selectCriteria,
  selectSelectedRegulatorNotices,
  selectPagination,
  selectResults,
  selectResultsLoaded,
  selectSortParameters,
  selectHideFilters,
  selectProcessTypesList,
} from '@regulator-notice-management/list/store';
import { RegulatorNoticeListResultsComponent } from '@regulator-notice-management/list/components/regulator-notice-list-results';
import { RegulatorNoticeListFormComponent } from '@regulator-notice-management/list/components/regulator-notice-list-form';
import { REGULATOR_NOTICE_LIST_PATH } from '@regulator-notice-management/list/regulator-notice-list.const';

@Component({
  selector: 'app-regulator-notice-list-container',
  templateUrl: './regulator-notice-list-container.component.html',
  providers: [SortService],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    SharedModule,
    RegulatorNoticeListResultsComponent,
    RegulatorNoticeListFormComponent,
    BulkActionsButtonGroupComponent,
    BulkActionSuccessComponent,
  ],
})
export class RegulatorNoticeListContainerComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly store = inject(Store);
  private readonly router = inject(Router);
  readonly pagination$ = this.store.select(selectPagination);
  readonly results$ = this.store.select(selectResults);
  readonly hideFilters$ = this.store.select(selectHideFilters);
  readonly resultsLoaded$ = this.store.select(selectResultsLoaded);
  readonly storedCriteria$ = this.store.select(selectCriteria);
  readonly selectedTasks$ = this.store.select(selectSelectedRegulatorNotices);
  readonly sortParameters$ = this.store.select(selectSortParameters);
  readonly isReportSuccess$ = this.store.select(selectIsReportSuccess);
  readonly processTypesList$ = this.store.select(selectProcessTypesList);

  page: number;
  pageSize = 10;
  criteria: RegulatorNoticeSearchCriteria;
  sortParameters: SortParameters = {
    sortField: 'initiatedDate',
    sortDirection: 'DESC',
  };
  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  loadPageParametersFromState = true;
  disableClaimAssignButtons = false;
  potentialErrors: Map<any, ErrorDetail>;

  ngOnInit() {
    this.store.dispatch(loadRequestAllocationData());
    this.store.dispatch(
      BulkActions.SET_CONFIG_VALUES({
        itemTypeLabel: 'notice',
        listPath: REGULATOR_NOTICE_LIST_PATH,
      })
    );

    this.route.data.subscribe((data: Data) => {
      this.potentialErrors = data['errorMap'];
    });
  }

  onSubmitClick() {
    this.loadPageParametersFromState = false;
  }

  onSearchTasks(
    criteria: RegulatorNoticeSearchCriteria,
    sortParameters: SortParameters,
    isReport?: boolean
  ) {
    this.store.dispatch(RegulatorNoticeListActions.CLEAR_SELECTION());
    this.sortParameters = sortParameters;
    this.criteria = criteria;
    this.searchRegulatorNotices(
      RegulatorNoticeListActions.SEARCH_REGULATOR_NOTICES,
      { page: 0, pageSize: this.pageSize },
      isReport,
      this.loadPageParametersFromState
    );
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.searchRegulatorNotices(
      RegulatorNoticeListActions.SORT_RESULTS,
      { page: 0, pageSize: this.pageSize },
      false,
      true
    );
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.searchRegulatorNotices(
      RegulatorNoticeListActions.SELECT_FIRST_RESULTS_PAGE,
      pageParameters
    );
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.searchRegulatorNotices(
      RegulatorNoticeListActions.SELECT_LAST_RESULTS_PAGE,
      pageParameters
    );
  }

  goToPageOfResults(pageParameters: PageParameters) {
    this.searchRegulatorNotices(
      RegulatorNoticeListActions.SELECT_RESULTS_PAGE,
      pageParameters
    );
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.searchRegulatorNotices(
      RegulatorNoticeListActions.SELECT_NEXT_RESULTS_PAGE,
      pageParameters
    );
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.searchRegulatorNotices(
      RegulatorNoticeListActions.SELECT_PREVIOUS_RESULTS_PAGE,
      pageParameters
    );
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.searchRegulatorNotices(
      RegulatorNoticeListActions.CHANGE_PAGE_SIZE,
      pageParameters
    );
  }

  private searchRegulatorNotices(
    action,
    pageParameters: PageParameters,
    isReport?: boolean,
    loadPageParametersFromState?: boolean
  ) {
    this.store.dispatch(clearErrors());
    const payload: SearchRegulatorNoticesActionPayload = {
      criteria: this.criteria,
      pageParameters,
      sortParameters: this.sortParameters,
      potentialErrors: this.potentialErrors,
      isReport,
      loadPageParametersFromState,
    };
    this.store.dispatch(action(payload));
  }

  toggleFilters() {
    this.store.dispatch(RegulatorNoticeListActions.TOGGLE_FILTERS());
  }

  onSelectedTasksChanged($event: SelectionChange<RegulatorNoticeTask>) {
    this.store.dispatch(
      RegulatorNoticeListActions.UPDATE_SELECTED({
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
        goBackRoute: '/' + REGULATOR_NOTICE_LIST_PATH,
        extras: {
          queryParams: { mode: ListMode.CACHE },
        },
      })
    );
  }

  requestReport(criteria: RegulatorNoticeSearchCriteria) {
    this.onSearchTasks(criteria, this.sortParameters, true);
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = { errors: value };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
