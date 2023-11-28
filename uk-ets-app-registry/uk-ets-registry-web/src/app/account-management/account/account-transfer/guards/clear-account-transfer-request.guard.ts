import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { clearAccountTransferRequest } from '@account-transfer/store/actions/account-transfer.actions';

@Injectable({
  providedIn: 'root',
})
export class ClearAccountTransferRequestGuard {
  constructor(private store: Store) {}

  canDeactivate(
    component: unknown,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    this.store.dispatch(clearAccountTransferRequest());
    return true;
  }
}
