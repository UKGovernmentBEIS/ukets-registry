import { Injectable } from '@angular/core';
import { SearchResolver } from '@shared/resolvers/search.resolver';
import { TransactionListState } from './transaction-list.reducer';
import { Action, MemoizedSelector, Store } from '@ngrx/store';
import * as TransactionListActions from './transaction-list.actions';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { selectSearchState } from './transaction-list.selector';
import { TransactionSearchCriteria } from '@shared/model/transaction';
import { Router } from '@angular/router';

@Injectable()
export class TransactionListResolver extends SearchResolver<
  TransactionSearchCriteria,
  TransactionListState
> {
  constructor(protected store: Store, private router: Router) {
    super();
  }

  protected doClearStateAction(): Action {
    return TransactionListActions.clearState();
  }

  protected doPostResultsLoadedAction(): void {
    return this.store.dispatch(TransactionListActions.resetSuccess());
  }

  protected doResetResultsLoadedAction(): Action {
    return TransactionListActions.resetResultsLoaded();
  }

  protected doSearchAction(payload: {
    criteria: TransactionSearchCriteria;
    pageParameters: PageParameters;
    sortParameters: SortParameters;
    potentialErrors: any;
  }): Action {
    const searchCriteria: any = this.router.getCurrentNavigation().extras
      ?.state;

    if (searchCriteria) {
      payload = { ...payload, criteria: searchCriteria };
    }

    return TransactionListActions.replaySearch(payload);
  }

  protected getSearchStateSelector(): MemoizedSelector<
    TransactionListState,
    TransactionListState
  > {
    return selectSearchState;
  }

  protected preDispatchSearchAction(): void {
    return;
  }
}
