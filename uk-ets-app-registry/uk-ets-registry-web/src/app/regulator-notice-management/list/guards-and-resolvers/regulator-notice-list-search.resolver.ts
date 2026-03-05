import { inject, Injectable } from '@angular/core';
import { Action, MemoizedSelector, Store } from '@ngrx/store';
import { SearchResolver } from '@shared/resolvers/search.resolver';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { RegulatorNoticeSearchCriteria } from '@shared/task-and-regulator-notice-management/model/regulator-notice-list.model';
import {
  RegulatorNoticeListActions,
  RegulatorNoticeListState,
  selectRegulatorNoticeListState,
} from '@regulator-notice-management/list/store';
import { BulkActions } from '@shared/task-and-regulator-notice-management/bulk-actions/store';

@Injectable()
export class RegulatorNoticeSearchResolver extends SearchResolver<
  RegulatorNoticeSearchCriteria,
  RegulatorNoticeListState
> {
  protected store = inject(Store);

  protected doClearStateAction(): Action {
    return RegulatorNoticeListActions.CLEAR_SEARCH_STATE();
  }

  protected doPostResultsLoadedAction(): void {
    this.store.dispatch(BulkActions.RESET_SUCCESS());
  }

  protected doResetResultsLoadedAction(): Action {
    return RegulatorNoticeListActions.RESET_RESULTS_LOADED();
  }

  protected doSearchAction(payload: {
    criteria: RegulatorNoticeSearchCriteria;
    pageParameters: PageParameters;
    sortParameters: SortParameters;
    potentialErrors: any;
  }): Action {
    return RegulatorNoticeListActions.REPLAY_SEARCH(payload);
  }

  protected getSearchStateSelector(): MemoizedSelector<
    RegulatorNoticeListState,
    RegulatorNoticeListState
  > {
    return selectRegulatorNoticeListState;
  }

  protected preDispatchSearchAction(): void {
    this.store.dispatch(RegulatorNoticeListActions.CLEAR_SELECTION());
  }
}
