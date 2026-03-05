import { Injectable } from '@angular/core';
import { Action, MemoizedSelector, Store } from '@ngrx/store';
import { SearchResolver } from '@shared/resolvers/search.resolver';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { BulkActions } from '@shared/task-and-regulator-notice-management/bulk-actions/store/bulk-actions.actions';
import { TaskListState } from '@task-management/task-list/store/task-list.reducer';
import { selectSearchState } from '@task-management/task-list/store/task-list.selector';
import * as TaskListActions from '@task-management/task-list/store/task-list.actions';
import { TaskSearchCriteria } from '@shared/task-and-regulator-notice-management/model';

@Injectable()
export class TaskSearchResolver extends SearchResolver<
  TaskSearchCriteria,
  TaskListState
> {
  constructor(protected store: Store) {
    super();
  }

  protected doClearStateAction(): Action {
    return TaskListActions.clearState();
  }

  protected doPostResultsLoadedAction(): void {
    this.store.dispatch(BulkActions.RESET_SUCCESS());
  }

  protected doResetResultsLoadedAction(): Action {
    return TaskListActions.resetResultsLoaded();
  }

  protected doSearchAction(payload: {
    criteria: TaskSearchCriteria;
    pageParameters: PageParameters;
    sortParameters: SortParameters;
    potentialErrors: any;
  }): Action {
    return TaskListActions.replaySearch(payload);
  }

  protected getSearchStateSelector(): MemoizedSelector<
    TaskListState,
    TaskListState
  > {
    return selectSearchState;
  }

  protected preDispatchSearchAction(): void {
    this.store.dispatch(TaskListActions.clearTasksSelection());
  }
}
