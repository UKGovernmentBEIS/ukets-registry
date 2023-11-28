import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  selectAuthorisedRepresentativeViewOrCheck,
  selectCurrentAuthorisedRepresentative,
  selectCurrentAuthorisedRepresentativeAccessRightText,
} from '../authorised-representative.selector';
import { ViewOrCheck } from '../../account-opening.model';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AuthorisedRepresentative } from '@shared/model/account/authorised-representative';
import { AuthorisedRepresentativeWizardRoutes } from '../authorised-representative-wizard-properties';
import { take } from 'rxjs/operators';
import { canGoBack, clearErrors } from '@shared/shared.action';
import { MainWizardRoutes } from '../../main-wizard.routes';
import {
  addCurrentARToList,
  removeARFromList,
} from '../authorised-representative.actions';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
})
export class OverviewComponent implements OnInit {
  authorisedRepresentativeViewOrCheck$: Observable<ViewOrCheck> = this.store.select(
    selectAuthorisedRepresentativeViewOrCheck
  ) as Observable<ViewOrCheck>;
  currentAuthorisedRepresentative$: Observable<AuthorisedRepresentative> = this.store.select(
    selectCurrentAuthorisedRepresentative
  ) as Observable<AuthorisedRepresentative>;

  viewOrCheck = ViewOrCheck;
  accessRightText$ = this.store.select(
    selectCurrentAuthorisedRepresentativeAccessRightText
  );

  authorisedRepresentativeWizardRoutes = AuthorisedRepresentativeWizardRoutes;
  readonly accessRightsRoute =
    AuthorisedRepresentativeWizardRoutes.ACCESS_RIGHTS;
  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;

  constructor(
    private _router: Router,
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.authorisedRepresentativeViewOrCheck$
      .pipe(take(1))
      .subscribe((viewOrCheck) => {
        if (viewOrCheck === ViewOrCheck.VIEW) {
          this.store.dispatch(
            canGoBack({
              goBackRoute: this.mainWizardRoute,
              extras: { skipLocationChange: true },
            })
          );
        } else {
          this.store.dispatch(
            canGoBack({
              goBackRoute: this.accessRightsRoute,
              extras: { skipLocationChange: true },
            })
          );
        }
      });
  }

  onEdit() {
    this._router.navigate([this.accessRightsRoute], {
      skipLocationChange: true,
    });
  }

  onApply() {
    this.store.dispatch(addCurrentARToList());
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }

  onDelete() {
    this.store.dispatch(removeARFromList());
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }
}
