import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Data, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { SortService } from '@shared/search/sort/sort.service';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { clearErrors, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';

import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import {
  MessageSearchCriteria,
  MessageSearchResult,
} from '@kp-administration/itl-messages/model';
import {
  changePageSize,
  hideCriteria,
  navigateToFirstPageOfResults,
  navigateToLastPageOfResults,
  navigateToNextPageOfResults,
  navigateToPageOfResults,
  navigateToPreviousPageOfResults,
  searchMessages,
  selectCriteria,
  selectHideCriteria,
  selectPagination,
  selectResults,
  selectResultsLoaded,
  selectSortParameters,
  showCriteria,
  sortResults,
} from '@kp-administration/store';
import { isReadOnlyAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-message-list-container',
  templateUrl: './message-list-container.component.html',
  providers: [SortService],
})
export class MessageListContainerComponent implements OnInit {
  page: number;
  pageSize = 10;
  criteria: MessageSearchCriteria;
  sortParameters: SortParameters;
  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  loadPageParametersFromState = true;

  pagination$: Observable<Pagination>;
  results$: Observable<MessageSearchResult[]>;
  hideCriteria$: Observable<boolean>;
  resultsLoaded$: Observable<boolean>;
  isReadOnlyAdmin$: Observable<boolean>;
  storedCriteria$: Observable<MessageSearchCriteria>;
  potentialErrors: Map<any, ErrorDetail>;
  sortParameters$: Observable<SortParameters>;

  constructor(
    private route: ActivatedRoute,
    private store: Store,
    private router: Router
  ) {}

  ngOnInit() {
    this.pagination$ = this.store.select(selectPagination);
    this.results$ = this.store.select(selectResults);
    this.hideCriteria$ = this.store.select(selectHideCriteria);
    this.resultsLoaded$ = this.store.select(selectResultsLoaded);
    this.storedCriteria$ = this.store.select(selectCriteria);
    this.isReadOnlyAdmin$ = this.store.select(isReadOnlyAdmin);
    this.sortParameters$ = this.store.select(selectSortParameters);
    this.route.data.subscribe((data: Data) => {
      this.potentialErrors = data['errorMap'];
    });

    this.sortParameters = {
      sortField: 'messageDate',
      sortDirection: 'DESC',
    };
  }

  searchMessages(
    action,
    pageParameters: PageParameters,
    loadPageParametersFromState?: boolean
  ) {
    this.store.dispatch(clearErrors());
    const payload = {
      criteria: this.criteria,
      pageParameters,
      sortParameters: this.sortParameters,
      potentialErrors: this.potentialErrors,
      loadPageParametersFromState,
    };
    this.store.dispatch(action(payload));
  }

  onSearchMessages(criteria: MessageSearchCriteria) {
    this.criteria = criteria;
    this.searchMessages(
      searchMessages,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      this.loadPageParametersFromState
    );
  }

  onSubmitClick() {
    this.loadPageParametersFromState = false;
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.searchMessages(
      sortResults,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      true
    );
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.searchMessages(navigateToFirstPageOfResults, pageParameters);
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.searchMessages(navigateToLastPageOfResults, pageParameters);
  }

  goToPageOfResults(pageParameters: PageParameters) {
    this.searchMessages(navigateToPageOfResults, pageParameters);
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.searchMessages(navigateToNextPageOfResults, pageParameters);
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.searchMessages(navigateToPreviousPageOfResults, pageParameters);
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.searchMessages(changePageSize, pageParameters);
  }

  hideShowCriteria($event: boolean) {
    $event
      ? this.store.dispatch(hideCriteria())
      : this.store.dispatch(showCriteria());
  }

  navigateToSendMessage() {
    this.router.navigate(['/kpadministration/send-itl-message'], {
      skipLocationChange: true,
    });
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
