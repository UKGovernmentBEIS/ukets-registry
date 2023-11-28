import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearOperatorUpdateRequest } from '@operator-update/actions/operator-update.actions';
import { clearTalTransactionRulesUpdateRequest } from '@tal-transaction-rules/actions/tal-transaction-rules.actions';

@Injectable({
  providedIn: 'root',
})
export class ClearUpdateTalTransactionRulesGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearTalTransactionRulesUpdateRequest());
    return true;
  }
}
