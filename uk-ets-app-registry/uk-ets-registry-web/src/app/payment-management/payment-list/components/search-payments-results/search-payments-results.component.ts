import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Location } from '@angular/common';
import { Router, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { PaymentSearchResult } from '@payment-management/model';
import { SearchMode } from '@shared/resolvers/search.resolver';
import { canGoBackToList } from '@shared/shared.action';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { PaymentMethod, PaymentStatus } from '@request-payment/model';

@Component({
  selector: 'app-search-payments-results',
  templateUrl: './search-payments-results.component.html',
  styles: ``,
})
export class SearchPaymentsResultsComponent {
  @Input()
  results: PaymentSearchResult[];
  @Input()
  sortParameters: SortParameters;
  @Input()
  goBackToListRoute: string;
  @Input()
  isSortable = true;
  @Input()
  isAdmin: boolean;

  @Output()
  readonly sort = new EventEmitter<SortParameters>();
  @Output()
  readonly openTaskDetail = new EventEmitter<string>();

  url: string;

  constructor(
    private router: Router,
    private location: Location,
    private store: Store
  ) {
    const snapshot: RouterStateSnapshot = router.routerState.snapshot;
    this.url =
      snapshot.url.indexOf('?') > -1 ? snapshot.url.split('?')[0] : null;
  }

  paymentMethodLabel(method = PaymentMethod.DEFAULT) {
    return PaymentMethod.label(method);
  }

  paymentStatusLabel(status = PaymentStatus.DEFAULT) {
    return PaymentStatus.label(status);
  }

  paymentTypeLabel(type) {
    return 'Payment';
  }

  paymentStatusColor(status = PaymentStatus.DEFAULT) {
    return PaymentStatus.color(status);
  }

  changeLocationState(): void {
    // We need to replace the url with the appropriate param mode when entering
    // a detailed page so to load the stored criteria when returning back to results by clicking on the browser's back button
    if (this.url) {
      this.location.go(this.url, `mode=${SearchMode.LOAD}`);
    }
  }

  navigateToTask(requestId: string, event): void {
    event.preventDefault();
    this.changeLocationState();
    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: this.goBackToListRoute,
        extras: {
          skipLocationChange: false,
          queryParams: {
            mode: SearchMode.LOAD,
          },
        },
      })
    );
    this.openTaskDetail.emit(requestId);
  }
}
