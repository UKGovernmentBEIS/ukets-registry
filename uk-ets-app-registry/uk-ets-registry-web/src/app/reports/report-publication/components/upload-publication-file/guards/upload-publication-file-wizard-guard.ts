import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';
import { clearPublicationFileWizard } from '@report-publication/components/upload-publication-file/actions/upload-publication-file.actions';

@Injectable({
  providedIn: 'root',
})
export class UploadPublicationFileWizardGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearPublicationFileWizard());
    return true;
  }
}
