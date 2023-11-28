import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { Store } from '@ngrx/store';
import { selectNoticeByIndex } from '@kp-administration/store';
import { catchError, map } from 'rxjs/operators';

@Injectable()
export class NoticeDetailsGuard {
  constructor(private readonly router: Router, private readonly store: Store) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> {
    return this.store
      .select(selectNoticeByIndex, {
        index: next.paramMap.get('typeId'),
      })
      .pipe(
        map((notice) => notice !== undefined),
        catchError(() =>
          of(
            this.router.parseUrl(
              state.url.slice(
                0,
                state.url.indexOf(next.url[next.url.length - 1].path) - 1
              )
            )
          )
        )
      );
  }
}
