import { TaskListState } from '../task-list.reducer';
import { Action, MemoizedSelector, Store } from '@ngrx/store';
import { selectSearchState } from '@task-management/task-list/task-list.selector';
import * as TaskListActions from '@task-management/task-list/task-list.actions';
import { Injectable } from '@angular/core';
import { TaskSearchCriteria } from '@task-management/model';
import { SearchResolver } from '@shared/resolvers/search.resolver';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';

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
    return this.store.dispatch(TaskListActions.resetSuccess());
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
