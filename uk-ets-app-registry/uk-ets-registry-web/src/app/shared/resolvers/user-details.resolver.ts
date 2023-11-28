import { DetailsResolver } from '@shared/resolvers/details.resolver';
import {
  areUserDetailsLoaded,
  UserDetailsState
} from '@user-management/user-details/store/reducers';
import { MemoizedSelector, Store } from '@ngrx/store';
import { ActivatedRouteSnapshot } from '@angular/router';
import { prepareNavigationToUserDetails } from '@user-management/user-details/store/actions';

export abstract class UserDetailsResolver extends DetailsResolver<
  UserDetailsState
> {
  protected constructor(protected store: Store) {
    super(store);
  }

  protected getDetailsLoadedFlagSelector(): MemoizedSelector<
    UserDetailsState,
    boolean
  > {
    return areUserDetailsLoaded;
  }

  protected loadDetails(route: ActivatedRouteSnapshot) {
    this.store.dispatch(
      prepareNavigationToUserDetails({
        urid: route.params.urid,
        backRoute: this.getBackRoute(route)
      })
    );
  }

  protected abstract getBackRoute(route: ActivatedRouteSnapshot): string;
}
