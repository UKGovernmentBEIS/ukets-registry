import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@registry-web/shared/shared.action';
import { clearReportPublication } from '@report-publication/actions';

@Injectable({
  providedIn: 'root',
})
export class ClearReportPublicationGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearReportPublication());
    this.store.dispatch(
      canGoBack({
        goBackRoute: null,
      })
    );
    return true;
  }
}
