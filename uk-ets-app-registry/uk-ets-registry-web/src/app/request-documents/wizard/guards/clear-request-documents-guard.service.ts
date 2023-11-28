import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearRequestDocuments } from '@request-documents/wizard/actions';

@Injectable({
  providedIn: 'root',
})
export class ClearRequestDocumentsGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearRequestDocuments());
    return true;
  }
}
