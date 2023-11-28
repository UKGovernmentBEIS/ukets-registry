import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearUploadedDocuments } from '@task-details/actions/task-details.actions';

@Injectable({
  providedIn: 'root',
})
export class UploadedDocumentsGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    if (
      !nextState.url.startsWith('/task-details') ||
      currentState.url.endsWith('/complete')
    ) {
      this.store.dispatch(clearUploadedDocuments());
    }
    return true;
  }
}
