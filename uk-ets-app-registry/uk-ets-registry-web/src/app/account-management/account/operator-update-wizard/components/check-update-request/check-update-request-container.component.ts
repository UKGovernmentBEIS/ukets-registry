import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack, errors } from '@shared/shared.action';
import {
  selectCurrentOperatorInfo,
  selectOperator,
} from '@account-management/account/operator-update-wizard/reducers';
import { Observable } from 'rxjs';
import { Operator } from '@shared/model/account';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { OperatorUpdate } from '@operator-update/model/operator-update';
import {
  cancelClicked,
  submitUpdateRequest,
} from '@operator-update/actions/operator-update.actions';
import { OperatorUpdateWizardPathsModel } from '@operator-update/model/operator-update-wizard-paths.model';
import { OperatorUpdateActions } from '@operator-update/actions';

@Component({
  selector: 'app-check-update-request-container',
  template: `<app-check-update-request
    [newOperatorInfo]="newOperatorInfo$ | async"
    [currentOperatorInfo]="currentOperatorInfo$ | async"
    (cancelEmitter)="onCancel()"
    (errorDetails)="onError($event)"
    (submitRequest)="onSubmit($event)"
    (navigateToEmitter)="navigateTo($event)"
  ></app-check-update-request>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckUpdateRequestContainerComponent implements OnInit {
  newOperatorInfo$: Observable<Operator>;
  currentOperatorInfo$: Observable<Operator>;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${OperatorUpdateWizardPathsModel.BASE_PATH}`,
        extras: { skipLocationChange: true },
      })
    );
    this.newOperatorInfo$ = this.store.select(selectOperator);
    this.currentOperatorInfo$ = this.store.select(selectCurrentOperatorInfo);
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onSubmit(value: OperatorUpdate) {
    this.store.dispatch(submitUpdateRequest({ operatorUpdate: value }));
  }

  navigateTo(routePath: string) {
    this.store.dispatch(
      OperatorUpdateActions.navigateTo({
        route: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${routePath}`,
        extras: { skipLocationChange: true },
      })
    );
  }
}
