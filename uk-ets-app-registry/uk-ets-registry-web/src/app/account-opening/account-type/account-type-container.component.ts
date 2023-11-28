import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { setAccountType } from '../account-opening.actions';
import { MainWizardRoutes } from '../main-wizard.routes';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { Observable, take } from 'rxjs';
import { isSeniorOrJuniorAdmin } from '@registry-web/auth/auth.selector';
import { AccountType } from '@shared/model/account';
import { selectEtrAddress } from '@registry-web/shared/shared.selector';
import {
  convertStringToJson,
  getConfigurationValue,
} from '@registry-web/shared/shared.util';
import { Configuration } from '@registry-web/shared/configuration/configuration.interface';
import { selectConfigurationRegistry } from '@registry-web/shared/shared.selector';

@Component({
  selector: 'app-account-type-container',
  template: ` <app-account-type
    (selectedAccountType)="onContinue($event)"
    (errorDetails)="onError($event)"
    [isAdmin]="isSeniorOrJuniorAdmin$ | async"
    [helpdeskEmail]="helpdeskEmail$ | async"
  >
  </app-account-type>`,
})
export class AccountTypeContainerComponent implements OnInit {
  readonly nextRoute = MainWizardRoutes.TASK_LIST;

  isSeniorOrJuniorAdmin$: Observable<boolean>;
  helpdeskEmail$: Observable<string>;
  configuration$: Observable<Configuration[]> = this.store.select(
    selectConfigurationRegistry
  );

  minNumberOfARs: number;
  maxNumberOfARs: number;
  minNumberOfARsForAccountOpeningPerType: Map<string, number>;
  maxNumberOfARsForAccountOpeningPerType: Map<string, number>;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.isSeniorOrJuniorAdmin$ = this.store.select(isSeniorOrJuniorAdmin);
    this.helpdeskEmail$ = this.store.select(selectEtrAddress);
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(clearErrors());
  }

  onContinue(value: AccountType) {
    this.configuration$.pipe(take(1)).subscribe((configuration) => {
      this.minNumberOfARs = getConfigurationValue(
        'business.property.account.min.number.of.authorised.representatives',
        configuration
      );
      this.maxNumberOfARs = getConfigurationValue(
        'business.property.account.max.number.of.authorised.representatives',
        configuration
      );
      this.minNumberOfARsForAccountOpeningPerType = convertStringToJson(
        getConfigurationValue(
          'business.property.account.opening.min.number.of.authorised.representatives.per.account.type.map',
          configuration
        )
      );
      this.maxNumberOfARsForAccountOpeningPerType = convertStringToJson(
        getConfigurationValue(
          'business.property.account.opening.max.number.of.authorised.representatives.per.account.type.map',
          configuration
        )
      );
    });
    const minNumberOfARsForAccountOpening =
      this.calculateMinNumberOfARsForAccountOpening(
        this.minNumberOfARsForAccountOpeningPerType,
        value,
        this.minNumberOfARs
      );
    const maxNumberOfARsForAccountOpening =
      this.calculateMaxNumberOfARsForAccountOpening(
        this.maxNumberOfARsForAccountOpeningPerType,
        value,
        this.maxNumberOfARs
      );
    this.store.dispatch(
      setAccountType({
        accountType: value,
        minNumberOfARs: minNumberOfARsForAccountOpening,
        maxNumberOfARs: maxNumberOfARsForAccountOpening,
      })
    );
    this._router.navigate([this.nextRoute], {
      skipLocationChange: true,
    });
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  /**
   * Calculates the min number of ARs that is allowed for the account creation of the given account type
   * @param minNumberOfARsForAccountOpeningPerType The min number of ARs that is allowed in the account creation per account type
   * @param accountType The account type
   * @param minNumberOfARs The min number of ARs that is allowed in an account
   */
  calculateMinNumberOfARsForAccountOpening(
    minNumberOfARsForAccountOpeningPerType: Map<string, number>,
    accountType: AccountType,
    minNumberOfARs: number
  ): number {
    return minNumberOfARsForAccountOpeningPerType != null &&
      minNumberOfARsForAccountOpeningPerType[accountType] !== undefined
      ? minNumberOfARsForAccountOpeningPerType[accountType]
      : minNumberOfARs;
  }

  /**
   * Calculates the min number of ARs that is allowed for the account creation of the given account type
   * @param maxNumberOfARsForAccountOpeningPerType The max number of ARs that is allowed in the account creation per account type
   * @param accountType The account type
   * @param maxNumberOfARs The max number of ARs that is allowed in an account
   */
  calculateMaxNumberOfARsForAccountOpening(
    maxNumberOfARsForAccountOpeningPerType: Map<string, number>,
    accountType: AccountType,
    maxNumberOfARs: number
  ): number {
    return maxNumberOfARsForAccountOpeningPerType != null &&
      maxNumberOfARsForAccountOpeningPerType[accountType] !== undefined &&
      maxNumberOfARsForAccountOpeningPerType[accountType] <= maxNumberOfARs
      ? maxNumberOfARsForAccountOpeningPerType[accountType]
      : maxNumberOfARs;
  }
}
