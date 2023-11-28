import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Installation,
  InstallationTransfer,
  Operator,
  OperatorType,
} from '@shared/model/account/operator';
import {
  selectOperator,
  selectOperatorInputBackLink,
} from '@account-opening/operator/operator.selector';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { MainWizardRoutes } from '@account-opening/main-wizard.routes';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { canGoBack, errors } from '@shared/shared.action';
import { take } from 'rxjs/operators';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AccountOpeningOperatorActions } from '@account-opening/operator/actions';
import { AccountHolder } from '@registry-web/shared/model/account';
import { selectAccountHolder } from '@registry-web/account-opening/account-holder/account-holder.selector';

@Component({
  selector: 'app-installation-container',
  template: `
    <ng-container [ngSwitch]="(operator$ | async).type">
      <app-installation-input
        *ngSwitchCase="type?.INSTALLATION"
        [installation]="operator$ | async"
        [title]="'Add the installation and permit details'"
        [headerTitle]="'Add the installation information'"
        (installationOutput)="onContinue($event)"
        (errorDetails)="onErrors($event)"
      ></app-installation-input>
      <app-installation-transfer-input
        *ngSwitchCase="type?.INSTALLATION_TRANSFER"
        [title]="'Specify the installation details'"
        [headerTitle]="'Add the installation information'"
        [installation]="operator$ | async"
        [accountHolder]="accountHolder$ | async"
        (installationTransferEmitter)="onContinueInstallationTransfer($event)"
        (errorDetails)="onErrors($event)"
      >
      </app-installation-transfer-input>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationContainerComponent implements OnInit {
  operator$: Observable<Operator>;
  accountHolder$: Observable<AccountHolder>;
  type = OperatorType;
  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly overviewRoute = OperatorWizardRoutes.OVERVIEW;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.operator$ = this.store.select(selectOperator);
    this.accountHolder$ = this.store.select(selectAccountHolder);
    this.store
      .select(selectOperatorInputBackLink)
      .pipe(take(1))
      .subscribe((backLink) => {
        this.store.dispatch(
          canGoBack({
            goBackRoute: backLink,
            extras: { skipLocationChange: true },
          })
        );
      });
    this.store.dispatch(
      AccountOpeningOperatorActions.completeWizard({ complete: false })
    );
  }

  onContinue(value: Installation) {
    this.store.dispatch(
      AccountOpeningOperatorActions.fetchExistsInstallationPermiId({
        installation: value,
      })
    );
  }

  onContinueInstallationTransfer($event: {
    installationTransfer: InstallationTransfer;
    acquiringAccountHolderIdentifier: number;
  }) {
    this.store.dispatch(
      AccountOpeningOperatorActions.validateInstallationTransfer({
        installationTransfer: $event.installationTransfer,
        acquiringAccountHolderIdentifier:
          $event.acquiringAccountHolderIdentifier,
      })
    );
  }

  onErrors(value: ErrorDetail[]) {
    this.store.dispatch(
      errors({
        errorSummary: new ErrorSummary(value),
      })
    );
  }
}
