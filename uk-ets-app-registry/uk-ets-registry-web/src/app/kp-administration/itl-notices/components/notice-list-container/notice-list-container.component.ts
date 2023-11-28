import { Component, OnInit } from '@angular/core';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Store } from '@ngrx/store';
import {
  changeNoticesListPage,
  loadNotices,
  selectNotices,
  selectNoticesPagination,
  selectNoticesSortParams,
} from '@kp-administration/store';
import { Observable } from 'rxjs';
import { Notice } from '@kp-administration/itl-notices/model';

@Component({
  selector: 'app-notice-list-container',
  templateUrl: './notice-list-container.component.html',
})
export class NoticeListContainerComponent implements OnInit {
  private readonly firstPage = { page: 0, pageSize: 10 };
  private sortParameters: SortParameters = {
    sortField: 'notificationIdentifier',
    sortDirection: 'ASC',
  };

  notices$: Observable<Notice[]> = this.store.select(selectNotices);
  pagination$: Observable<Pagination> = this.store.select(
    selectNoticesPagination
  );

  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  sortParameters$: Observable<SortParameters> = this.store.select(
    selectNoticesSortParams
  );

  constructor(private readonly store: Store) {}

  ngOnInit() {
    this.store.dispatch(
      loadNotices({
        criteria: null,
        pageParameters: this.firstPage,
        sortParameters: this.sortParameters,
        potentialErrors: null,
        loadPageParametersFromState: true,
      })
    );
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.store.dispatch(
      changeNoticesListPage({
        sortParameters,
        pageParameters: this.firstPage,
        criteria: null,
        potentialErrors: null,
        loadPageParametersFromState: true,
      })
    );
  }

  onPageEvent(pageParameters: PageParameters) {
    this.store.dispatch(
      changeNoticesListPage({
        sortParameters: this.sortParameters,
        pageParameters,
        criteria: null,
        potentialErrors: null,
        loadPageParametersFromState: false,
      })
    );
  }
}
