import { Injectable } from '@angular/core';
import { SearchResolver } from '@shared/resolvers/search.resolver';
import { AccountSearchCriteria } from './account-list.model';
import { AccountListState } from './account-list.reducer';
import { Action, MemoizedSelector, Store } from '@ngrx/store';
import * as AccountListActions from './account-list.actions';
import { PageParameters } from '../../shared/search/paginator/paginator.model';
import { SortParameters } from '../../shared/search/sort/SortParameters';
import { selectSearchState } from './account-list.selector';
import { ActivatedRouteSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { AuthApiService } from '../../auth/auth-api.service';
import { concatMap } from 'rxjs/operators';

@Injectable()
export class AccountListResolver extends SearchResolver<
  AccountSearchCriteria,
  AccountListState
> {
  constructor(protected store: Store, private authApiService: AuthApiService) {
    super();
  }

  protected resolveDefault(
    route: ActivatedRouteSnapshot
  ): Observable<any> | any {
    super.resolveDefault(route);
    return this.authApiService
      .hasScope('urn:uk-ets-registry-api:actionForAnyAdmin')
      .pipe(
        concatMap((isAdmin) => {
          return isAdmin ? of(isAdmin) : this.reload(route);
        })
      );
  }

  protected doClearStateAction(): Action {
    return AccountListActions.clearState();
  }

  protected doPostResultsLoadedAction(): void {
    return this.store.dispatch(AccountListActions.resetSuccess());
  }

  protected doResetResultsLoadedAction(): Action {
    return AccountListActions.resetResultsLoaded();
  }

  protected doSearchAction(payload: {
    criteria: AccountSearchCriteria;
    pageParameters: PageParameters;
    sortParameters: SortParameters;
    potentialErrors: any;
  }): Action {
    return AccountListActions.replaySearch(payload);
  }

  protected getSearchStateSelector(): MemoizedSelector<
    AccountListState,
    AccountListState
  > {
    return selectSearchState;
  }

  protected preDispatchSearchAction(): void {
    return;
  }
}
