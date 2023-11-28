import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';
import { clearPublicationDetailsWizard } from '@report-publication/components/update-publication-details/actions/update-publication-details.actions';

@Injectable({
  providedIn: 'root',
})
export class ClearPublicationDetailsWizardGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearPublicationDetailsWizard());
    return true;
  }
}
