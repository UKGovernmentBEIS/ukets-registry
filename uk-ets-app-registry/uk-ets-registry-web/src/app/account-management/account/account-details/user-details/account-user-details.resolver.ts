import { Injectable } from '@angular/core';
import { UserDetailsResolver } from '@shared/resolvers/user-details.resolver';
import { Store } from '@ngrx/store';
import { ActivatedRouteSnapshot } from '@angular/router';

@Injectable()
export class AccountUserDetailsResolver extends UserDetailsResolver {
  constructor(protected store: Store) {
    super(store);
  }

  protected getBackRoute(route: ActivatedRouteSnapshot): string {
    return `/account/${route.params.accountId}`;
  }
}
