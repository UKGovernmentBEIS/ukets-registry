import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors } from '@registry-web/shared/shared.action';
import {
  clearDeleteFile,
  clearDeleteFileName,
} from '@delete-file/wizard/actions/delete-file.actions';

@Injectable({
  providedIn: 'root',
})
export class ClearDeleteFileGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearDeleteFile());
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      canGoBack({
        goBackRoute: null,
      })
    );
    return true;
  }
}
