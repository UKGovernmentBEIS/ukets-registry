import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { resetState } from '../store/documents-wizard.actions';
import { DocumentsWizardPath } from '@registry-web/documents/models/documents-wizard-path.model';
import { clearGoBackRoute } from '@registry-web/shared/shared.action';

@Injectable()
export class DocumentsWizardGuard {
  constructor(private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    return of(true);
  }

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): Observable<boolean> {
    if (nextState?.url.indexOf(DocumentsWizardPath.BASE_PATH) === -1) {
      this.store.dispatch(resetState());
      this.clearGoBack();
    }
    return of(true);
  }

  clearGoBack() {
    this.store.dispatch(clearGoBackRoute());
  }
}
