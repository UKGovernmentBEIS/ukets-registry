import { DetailsResolver } from '@shared/resolvers/details.resolver';
import { AllocationStatusState } from '@account-management/account/allocation-status/reducers';
import { MemoizedSelector, Store } from '@ngrx/store';
import { ActivatedRouteSnapshot } from '@angular/router';
import { areAnnualAllocationStatusesLoaded } from '@account-management/account/allocation-status/reducers/allocation-status.selector';
import { prepareWizard } from '@account-management/account/allocation-status/actions/allocation-status.actions';
import { Injectable } from '@angular/core';

@Injectable()
export class AllocationStatusResolver extends DetailsResolver<
  AllocationStatusState
> {
  constructor(protected store: Store) {
    super(store);
  }

  protected getDetailsLoadedFlagSelector(): MemoizedSelector<
    AllocationStatusState,
    boolean
  > {
    return areAnnualAllocationStatusesLoaded;
  }

  protected loadDetails(route: ActivatedRouteSnapshot) {
    this.store.dispatch(prepareWizard({ accountId: route.params.accountId }));
  }
}
