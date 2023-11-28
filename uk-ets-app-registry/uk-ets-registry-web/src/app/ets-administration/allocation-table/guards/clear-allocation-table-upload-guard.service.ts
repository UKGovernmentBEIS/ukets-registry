import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearAllocationTableWizard } from '@allocation-table/actions/allocation-table.actions';

@Injectable({
  providedIn: 'root',
})
export class ClearAllocationTableUploadGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearAllocationTableWizard());
    return true;
  }
}
