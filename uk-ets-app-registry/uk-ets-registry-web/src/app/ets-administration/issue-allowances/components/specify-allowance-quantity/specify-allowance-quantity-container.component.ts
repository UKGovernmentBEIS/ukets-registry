import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import { AllowanceTransactionBlockSummary } from '@shared/model/transaction';
import {
  loadAllowanceWizardData,
  setAllowanceQuantity,
} from '@issue-allowances/actions/issue-allowance.actions';
import { Observable, of } from 'rxjs';
import {
  selectAllowanceBlocks,
  selectSelectedQuantity,
} from '@issue-allowances/reducers';
import { selectRegistryConfigurationProperty } from '@shared/shared.selector';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-specify-allowance-quantity-container',
  template: `
    <app-specify-allowance-quantity
      [transactionBlockSummaries]="allowanceTransactionBlockSummaries$ | async"
      [activeYear]="activeYear$ | async"
      [selectedQuantity]="selectedQuantity$ | async"
      (quantity)="setQuantityForAllowance($event)"
      (errorDetails)="onError($event)"
    ></app-specify-allowance-quantity>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SpecifyAllowanceQuantityContainerComponent implements OnInit {
  allowanceTransactionBlockSummaries$: Observable<
    Partial<AllowanceTransactionBlockSummary>[]
  >;
  activeYear$: Observable<number>;
  selectedQuantity$: Observable<string>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(loadAllowanceWizardData());
    this.activeYear$ = this.getActiveAllocationYear();

    this.selectedQuantity$ = this.store.select(selectSelectedQuantity);
    this.allowanceTransactionBlockSummaries$ = this.store.select(
      selectAllowanceBlocks
    );

    this.store.dispatch(canGoBack({ goBackRoute: null }));
  }

  setQuantityForAllowance({ quantity, year }) {
    this.store.dispatch(setAllowanceQuantity({ quantity, year }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  getActiveAllocationYear(): Observable<number> {
    let year = 2021;
    this.store
      .select(selectRegistryConfigurationProperty, {
        property: 'business.property.transaction.allocation-year',
      })
      .subscribe((value) => (year = Number(value)));
    if (year === undefined || isNaN(year) || year === 0) {
      year = new Date().getFullYear();
    }
    return of(year);
  }
}
