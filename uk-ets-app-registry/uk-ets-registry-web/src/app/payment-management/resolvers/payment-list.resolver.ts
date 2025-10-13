import { Injectable } from '@angular/core';
import * as PaymentListActions from '@payment-management/payment-list/store/actions/payment-list.actions';
import { SearchResolver } from '@shared/resolvers/search.resolver';
import { PaymentSearchCriteria } from '@payment-management/model';
import {
  PaymentListState,
  selectSearchState,
} from '@payment-management/payment-list/store/reducer';
import {
  Action,
  MemoizedSelector,
  DefaultProjectorFn,
  Store,
} from '@ngrx/store';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Router } from '@angular/router';

@Injectable()
export class PaymentListResolver extends SearchResolver<
  PaymentSearchCriteria,
  PaymentListState
> {
  constructor(
    protected store: Store,
    private router: Router
  ) {
    super();
  }

  protected preDispatchSearchAction(): void {
    return;
  }

  protected doSearchAction(payload: {
    criteria: PaymentSearchCriteria;
    pageParameters: PageParameters;
    sortParameters: SortParameters;
    potentialErrors: any;
  }): Action {
    const searchCriteria: any =
      this.router.getCurrentNavigation().extras?.state;
    if (searchCriteria) {
      payload = { ...payload, criteria: searchCriteria };
    }

    return PaymentListActions.replaySearch(payload);
  }

  protected doClearStateAction(): Action {
    return PaymentListActions.clearState();
  }

  protected doResetResultsLoadedAction(): Action {
    return PaymentListActions.resetResultsLoaded();
  }

  protected getSearchStateSelector(): MemoizedSelector<
    PaymentListState,
    PaymentListState,
    DefaultProjectorFn<PaymentListState>
  > {
    return selectSearchState;
  }

  protected doPostResultsLoadedAction(): void {
    return this.store.dispatch(PaymentListActions.resetSuccess());
  }
}
