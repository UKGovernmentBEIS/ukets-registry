import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { take } from 'rxjs/operators';
import {
  ARAccessRights,
  AuthorisedRepresentative,
} from '@shared/model/account/authorised-representative';
import {
  selectAuthorisedRepresentativeViewOrCheck,
  selectCurrentAuthorisedRepresentative,
  selectIfLessThanFiveARsWithOtherThanReadOnly,
} from '../authorised-representative.selector';
import { AuthorisedRepresentativeWizardRoutes } from '../authorised-representative-wizard-properties';
import {
  setAccessRightsToCurrentAR,
  setAuthorisedRepresentativeViewOrCheck,
} from '../authorised-representative.actions';
import { ViewOrCheck } from '../../account-opening.model';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { initAll } from 'govuk-frontend';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { selectIsOHAOrAOHAorMOHA } from '@registry-web/account-opening/account-opening.selector';

@Component({
  selector: 'app-access-rights-container',
  template: `
    <app-access-rights
      [authorisedRepresentative]="authorisedRepresentative$ | async"
      [showSurrender]="showSurrender$ | async"
      (selectedAccessRights)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-access-rights>
  `,
})
export class AccessRightsContainerComponent implements OnInit {
  authorisedRepresentative$: Observable<AuthorisedRepresentative> =
    this.store.select(
      selectCurrentAuthorisedRepresentative
    ) as Observable<AuthorisedRepresentative>;
  authorisedRepresentativeViewOrCheck$: Observable<ViewOrCheck> =
    this.store.select(
      selectAuthorisedRepresentativeViewOrCheck
    ) as Observable<ViewOrCheck>;
  authorisedRepresentatives$: Observable<AuthorisedRepresentative[]>;
  lessThanFiveARsWithOtherThanReadOnly$: Observable<boolean> =
    this.store.select(
      selectIfLessThanFiveARsWithOtherThanReadOnly
    ) as Observable<boolean>;
  showOtherThanReadOnlySelections$: Observable<boolean>;

  showSurrender$ = this.store.select(selectIsOHAOrAOHAorMOHA);

  arAccessRight: ARAccessRights;

  readonly selectionRoute = AuthorisedRepresentativeWizardRoutes.SELECTION;
  readonly overviewRoute = AuthorisedRepresentativeWizardRoutes.OVERVIEW;

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
              goBackRoute: this.overviewRoute,
              extras: { skipLocationChange: true },
            })
          );
        } else {
          this.store.dispatch(
            canGoBack({
              goBackRoute: this.selectionRoute,
              extras: { skipLocationChange: true },
            })
          );
        }
        this.authorisedRepresentative$.pipe(take(1)).subscribe((ar) => {
          this.arAccessRight = ar.right;
        });
      });
    this.store.dispatch(
      setAuthorisedRepresentativeViewOrCheck({ viewOrCheck: ViewOrCheck.CHECK })
    );
    this.lessThanFiveARsWithOtherThanReadOnly$
      .pipe(take(1))
      .subscribe((value) => {
        this.authorisedRepresentative$.pipe(take(1)).subscribe((ar) => {
          if (value || (ar.right && ar.right !== ARAccessRights.READ_ONLY)) {
            this.showOtherThanReadOnlySelections$ = of(true);
          } else {
            return of(false);
          }
        });
      });
    initAll();
  }

  onContinue(value: ARAccessRights) {
    this.store.dispatch(setAccessRightsToCurrentAR({ accessRights: value }));
    this._router.navigate([this.overviewRoute], { skipLocationChange: true });
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
