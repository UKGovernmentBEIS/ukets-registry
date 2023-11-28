import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  selectExistingAr,
  selectNewAccessRights,
  selectNewAr,
  selectSelectedArFromTable,
  selectUpdateType,
} from '@authorised-representatives/reducers';
import { ActivatedRoute, Data } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelClicked,
  submitUpdateRequest,
} from '@authorised-representatives/actions/authorised-representatives.actions';
import { AuthorisedRepresentativesRoutePaths } from '@authorised-representatives/model/authorised-representatives-route-paths.model';
import { AuthorisedRepresentativesActions } from '@authorised-representatives/actions';
import {
  ARAccessRights,
  AuthorisedRepresentative,
} from '@shared/model/account';
import { ErrorDetail } from '@shared/error-summary';

@Component({
  selector: 'app-check-update-request-container',
  template: `
    <app-check-ar-update-request
      [updateType]="updateType$ | async"
      [existingAr]="existingAr$ | async"
      [newAr]="newAr$ | async"
      [selectedArFromTable]="selectedArFromTable$ | async"
      [newAccessRights]="newAccessRights$ | async"
      (clickChange)="onChangeClicked($event)"
      (submitRequest)="onSubmit($event)"
      (errorDetails)="onError($event)"
    >
    </app-check-ar-update-request>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckUpdateRequestContainerComponent implements OnInit {
  updateType$: Observable<AuthorisedRepresentativesUpdateType>;
  existingAr$: Observable<AuthorisedRepresentative>;
  newAr$: Observable<AuthorisedRepresentative>;
  selectedArFromTable$: Observable<AuthorisedRepresentative>;
  newAccessRights$: Observable<ARAccessRights>;

  goBackPath: string;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.data.subscribe((data: Data) => {
      this.initData(data);
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/authorised-representatives/${this.goBackPath}`,
        extras: { skipLocationChange: true },
      })
    );
    this.updateType$ = this.store.select(selectUpdateType);
    this.existingAr$ = this.store.select(selectExistingAr);
    this.newAr$ = this.store.select(selectNewAr);
    this.selectedArFromTable$ = this.store.select(selectSelectedArFromTable);
    this.newAccessRights$ = this.store.select(selectNewAccessRights);
  }

  protected initData(data: Data) {
    this.goBackPath = data.goBackPath;
  }

  onChangeClicked(step: AuthorisedRepresentativesRoutePaths) {
    this.store.dispatch(
      AuthorisedRepresentativesActions.navigateTo({
        route: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/authorised-representatives/${step}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onSubmit(comment: string) {
    this.store.dispatch(submitUpdateRequest({ comment: comment }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(details: ErrorDetail[]) {
    this.store.dispatch(
      errors({
        errorSummary: {
          errors: details,
        },
      })
    );
  }
}
