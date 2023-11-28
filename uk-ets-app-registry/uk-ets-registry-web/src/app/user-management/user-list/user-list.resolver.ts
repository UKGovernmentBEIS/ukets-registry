import { Injectable } from '@angular/core';
import { SearchResolver } from '@shared/resolvers/search.resolver';
import { UserSearchCriteria } from './user-list.model';
import { UserListState } from './user-list.reducer';
import { Action, MemoizedSelector, Store } from '@ngrx/store';
import * as UserListActions from './user-list.actions';
import { PageParameters } from '../../shared/search/paginator/paginator.model';
import { SortParameters } from '../../shared/search/sort/SortParameters';
import { selectSearchState } from './user-list.selector';

@Injectable()
export class UserListResolver extends SearchResolver<
  UserSearchCriteria,
  UserListState
> {
  constructor(protected store: Store) {
    super();
  }

  protected doClearStateAction(): Action {
    return UserListActions.clearState();
  }

  protected doPostResultsLoadedAction(): void {
    return this.store.dispatch(UserListActions.resetSuccess());
  }

  protected doResetResultsLoadedAction(): Action {
    return UserListActions.resetResultsLoaded();
  }

  protected doSearchAction(payload: {
    criteria: UserSearchCriteria;
    pageParameters: PageParameters;
    sortParameters: SortParameters;
    potentialErrors: any;
  }): Action {
    return UserListActions.replaySearch(payload);
  }

  protected getSearchStateSelector(): MemoizedSelector<
    UserListState,
    UserListState
  > {
    return selectSearchState;
  }

  protected preDispatchSearchAction(): void {
    return;
  }
}
