import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { Installation, Operator } from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary';
import { selectOperator } from '@operator-update/reducers';
import {
  cancelClicked,
  setNewOperatorInfoSuccess,
} from '@operator-update/actions/operator-update.actions';
import { isSeniorOrJuniorAdmin } from '@registry-web/auth/auth.selector';
import { OperatorUpdateWizardPathsModel } from '@operator-update/model/operator-update-wizard-paths.model';

@Component({
  selector: 'app-operator-activity-type-update-container',
  template: ` <app-installation-activity-type
    [title]="'Update the regulated activity'"
    [headerTitle]="'Request to update the installation information'"
    [operator]="operatorInfo$ | async"
    (cancelEmitter)="onCancel()"
    (installationTypeEmitter)="onContinue($event)"
    (errorDetails)="onErrors($event)"
  >
  </app-installation-activity-type>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorActivityTypeUpdateContainerComponent implements OnInit {
  operatorInfo$: Observable<Operator>;
  isSeniorOrJuniorAdmin$: Observable<boolean>;

  constructor(
    private store: Store,
    private route: ActivatedRoute
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
    this.operatorInfo$ = this.store.select(selectOperator);
    this.isSeniorOrJuniorAdmin$ = this.store.select(isSeniorOrJuniorAdmin);
  }

  onContinue(value: Operator) {
    this.store.dispatch(
      setNewOperatorInfoSuccess({ operator: value as Installation })
    );
  }

  onErrors(value: ErrorSummary) {
    this.store.dispatch(
      errors({
        errorSummary: value,
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
