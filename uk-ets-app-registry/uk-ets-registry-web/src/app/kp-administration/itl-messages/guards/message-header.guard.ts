import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';

import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { MessageHeaderComponent } from '@kp-administration/itl-messages/components';
import {
  clearMessage,
  fetchMessage,
  selectMessage,
} from '@kp-administration/store';

@Injectable()
export class MessageHeaderGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: MessageHeaderComponent,
    route: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState: RouterStateSnapshot
  ): Observable<boolean> {
    this.store.dispatch(clearMessage());
    return of(true);
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const { messageId } = route.params;
    return this.getMessageDetails(messageId).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  private getMessageDetails(messageId: string) {
    return this.store.select(selectMessage).pipe(
      tap(() => this.prefetch(messageId)),
      filter((message) => {
        return message != null;
      }),
      take(1)
    );
  }

  private prefetch(messageId: string) {
    this.store.dispatch(fetchMessage({ messageId }));
  }
}
