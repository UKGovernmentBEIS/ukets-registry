import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { Action, MemoizedSelector, Store } from '@ngrx/store';
import * as AllocationJobStatusActions from '../store/allocation-job-status.actions';
import { SearchResolver } from '@registry-web/shared/resolvers/search.resolver';
import { AllocationJobSearchCriteria } from '../models/allocation-job-search-criteria.model';
import { AllocationJobStatusState } from '../store/allocation-job-status.reducer';
import { Observable, concatMap, of } from 'rxjs';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import { PageParameters } from '@registry-web/shared/search/paginator';
import { selectSearchState } from '../store/allocation-job-status.selectors';
import { AuthApiService } from '@registry-web/auth/auth-api.service';

@Injectable()
export class AllocationJobStatusResolver extends SearchResolver<
  AllocationJobSearchCriteria,
  AllocationJobStatusState
> {
  constructor(protected store: Store) {
    super();
  }

  protected doClearStateAction(): Action {
    return AllocationJobStatusActions.clearState();
  }

  protected doPostResultsLoadedAction(): void {
    //return this.store.dispatch(AllocationJobStatusActions.resetSuccess());
  }

  protected doResetResultsLoadedAction(): Action {
    return AllocationJobStatusActions.resetResultsLoaded();
  }

  protected doSearchAction(payload: {
    criteria: AllocationJobSearchCriteria;
    pageParameters: PageParameters;
    sortParameters: SortParameters;
    potentialErrors: any;
  }): Action {
    return AllocationJobStatusActions.replaySearch(payload);
  }

  protected getSearchStateSelector(): MemoizedSelector<
    AllocationJobStatusState,
    AllocationJobStatusState
  > {
    return selectSearchState;
  }

  protected preDispatchSearchAction(): void {
    return;
  }
}
