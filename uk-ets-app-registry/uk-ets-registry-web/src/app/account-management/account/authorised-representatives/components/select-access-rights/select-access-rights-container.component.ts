import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  selectNewAccessRights,
  selectNewArFullName,
  selectUpdateType,
} from '@authorised-representatives/reducers';
import { Observable } from 'rxjs';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { ActivatedRoute, Data } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import { ARAccessRights } from '@shared/model/account';
import {
  cancelClicked,
  setNewAccessRights,
} from '@authorised-representatives/actions/authorised-representatives.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { selectIsOHAOrAOHA } from '@registry-web/account-management/account/account-details/account.selector';

@Component({
  selector: 'app-select-access-rights-container',
  template: `
    <app-select-access-rights
      [updateType]="updateType$ | async"
      [arFullName]="arFullName$ | async"
      [accessRights]="accessRights$ | async"
      [showSurrender]="showSurrender$ | async"
      (selectAccessRights)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-select-access-rights>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectAccessRightsContainerComponent implements OnInit {
  updateType$: Observable<AuthorisedRepresentativesUpdateType>;
  arFullName$: Observable<string>;
  accessRights$: Observable<ARAccessRights>;
  showSurrender$: Observable<boolean>;

  goBackPath: string;

  constructor(private route: ActivatedRoute, private store: Store) {}

  ngOnInit() {
    this.route.data.subscribe((data: Data) => {
      this.initData(data);
    });
    this.updateType$ = this.store.select(selectUpdateType);
    this.arFullName$ = this.store.select(selectNewArFullName);
    this.accessRights$ = this.store.select(selectNewAccessRights);
    this.showSurrender$ = this.store.select(selectIsOHAOrAOHA);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/authorised-representatives/${this.goBackPath}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  protected initData(data: Data) {
    this.goBackPath = data.goBackPath;
  }

  onContinue(accessRights: ARAccessRights) {
    this.store.dispatch(setNewAccessRights({ accessRights }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
