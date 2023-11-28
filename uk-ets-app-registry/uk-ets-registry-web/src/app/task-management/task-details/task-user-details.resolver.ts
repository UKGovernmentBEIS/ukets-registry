import { ActivatedRouteSnapshot } from '@angular/router';
import { Injectable } from '@angular/core';
import { UserDetailsResolver } from '@shared/resolvers/user-details.resolver';
import { Store } from '@ngrx/store';

@Injectable()
export class TaskUserDetailsResolver extends UserDetailsResolver {
  constructor(protected store: Store) {
    super(store);
  }

  protected getBackRoute(route: ActivatedRouteSnapshot): string {
    return `/task-details/${route.params.requestId}`;
  }
}
