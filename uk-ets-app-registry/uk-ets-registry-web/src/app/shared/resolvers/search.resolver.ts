import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { filter, finalize, first, tap } from 'rxjs/operators';
import { canGoBack, clearErrors } from '../shared.action';
import { Action } from '@ngrx/store/src/models';
import { MemoizedSelector } from '@ngrx/store/src/selector';
import { PageParameters } from '../search/paginator/paginator.model';
import { SortParameters } from '../search/sort/SortParameters';

/**
 * The search mode enumeration
 */
export enum SearchMode {
  /**
   * Indicates that the search must be executed again with the stored criteria
   */
  LOAD,
  /***
   * Indicates that the the search should not be repeated and cached results should be returned
   */
  CACHE,
  /**
   * Indicates that the search must be executed with the default criteria
   */
  INITIAL_LOAD,
}

/**
 * Common properties that a search state should have
 */
export interface SearchState<C> {
  resultsLoaded: boolean;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  criteria: C;
}

/**
 * Abstract resolver for handling the results loading on search page load.
 */
export abstract class SearchResolver<C, S extends SearchState<C>> {
  private loading = false;
  private directUrlCall = false;
  protected store: Store;

  resolve(
    route: ActivatedRouteSnapshot,
    routerState: RouterStateSnapshot
  ): Observable<any> | any {
    this.clearSharedState();
    const searchMode = +route.queryParams['mode'];
    switch (searchMode) {
      case SearchMode.LOAD.valueOf(): {
        return this.reload(route);
      }
      case SearchMode.CACHE.valueOf(): {
        return;
      }
      case SearchMode.INITIAL_LOAD.valueOf(): {
        this.store.dispatch(this.doClearStateAction());
        return this.reload(route);
      }
      default: {
        return this.resolveDefault(route);
      }
    }
  }

  protected resolveDefault(
    route: ActivatedRouteSnapshot
  ): Observable<any> | any {
    this.store.dispatch(this.doClearStateAction());
    return null;
  }

  reload(route: ActivatedRouteSnapshot): Observable<S> {
    this.store.dispatch(this.doResetResultsLoadedAction());
    return this.store.pipe(
      select(this.getSearchStateSelector()),
      tap((state) => {
        this.search(state, route);
      }),
      filter((state) => state.resultsLoaded || this.directUrlCall),
      first(),
      finalize(() => {
        this.loading = false;
        this.doPostResultsLoadedAction();
      })
    );
  }

  search(state: S, route: ActivatedRouteSnapshot) {
    if (!this.shouldRepeatTheSearch(state)) {
      return;
    }
    this.loading = true;
    this.store.dispatch(clearErrors());
    this.preDispatchSearchAction();
    this.store.dispatch(
      this.doSearchAction({
        criteria: state.criteria,
        pageParameters: state.pageParameters,
        sortParameters: state.sortParameters,
        potentialErrors: route.data['errorMap'],
      })
    );
  }

  shouldRepeatTheSearch(state: S) {
    this.directUrlCall = !state.criteria && !state.pageParameters;
    return !this.directUrlCall && !this.loading && !state.resultsLoaded;
  }

  clearSharedState() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(clearErrors());
  }

  /**
   * Executes logic on before dispatching the search action
   */
  protected abstract preDispatchSearchAction(): void;

  /**
   * Returns the action of search
   * @param payload The props of the action
   */
  protected abstract doSearchAction(payload: {
    criteria: C;
    pageParameters: PageParameters;
    sortParameters: SortParameters;
    potentialErrors: any;
  }): Action;

  /**
   * Returns the action which clears the stored state
   */
  protected abstract doClearStateAction(): Action;

  /**
   * Returns the action which resets the results loaded flag to false
   */
  protected abstract doResetResultsLoadedAction(): Action;

  /**
   * Returns the store Selector of the state
   */
  protected abstract getSearchStateSelector(): MemoizedSelector<S, S>;

  /**
   * Executes logic immediately after search results are loaded
   */
  protected abstract doPostResultsLoadedAction(): void;
}
