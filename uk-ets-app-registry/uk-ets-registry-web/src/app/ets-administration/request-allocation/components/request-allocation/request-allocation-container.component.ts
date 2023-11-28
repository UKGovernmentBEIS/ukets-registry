import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import { Observable, Subject } from 'rxjs';
import {
  cancelPendingAllocationsResult,
  isAllocationPendingAndNoNotification,
  isCancelAllocationSuccessAndPendingAllocationsNotLoading,
  selectedAllocationCategory,
  selectedAllocationYear,
} from '@request-allocation/reducers';
import {
  cancelPendingAllocationsMessageTimeout,
  isPendingAllocationsRequested,
  selectAllocationCategory,
  selectAllocationYear,
} from '@request-allocation/actions';
import { loadRequestAllocationData } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { ActivatedRoute, Router } from '@angular/router';
import { delay, filter, first, takeUntil, tap } from 'rxjs/operators';
import { isSeniorAdmin } from '@registry-web/auth/auth.selector';
import { AllocationCategory } from '@registry-web/shared/model/allocation';
import { allocationYears } from '@registry-web/shared/shared.selector';

const pathCancelPendingAllocations = 'cancel-pending-allocations';
const PENDING_ALLOCATION_NOTIFICATION_DELAY = 2000;

@Component({
  selector: 'app-request-allocation-container',
  template: `
    <app-pending-request-allocation-warning
      *ngIf="isSeniorAdmin$ | async"
      [isCancelAllocationSuccessDisplayed]="
        isCancelAllocationSuccessDisplayed$ | async
      "
      [isAllocationPendingDisplayed]="isAllocationPendingDisplayed$ | async"
      (cancelAllocations)="onCancelAllocations()"
    >
    </app-pending-request-allocation-warning>
    <app-request-allocation
      *ngIf="allocationYears$ | async as allocationYears"
      [allocationYears]="allocationYears"
      [selectedAllocationYear]="selectedAllocationYear$ | async"
      [selectedAllocationCategory]="selectedAllocationCategory$ | async"
      (errorDetails)="onError($event)"
      (selectAllocationYear)="onSelectAllocationYear($event)"
      (selectAllocationCategory)="onSelectAllocationCategory($event)"
    ></app-request-allocation>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestAllocationContainerComponent implements OnInit, OnDestroy {
  allocationYears$: Observable<number[]>;
  selectedAllocationYear$: Observable<number>;
  selectedAllocationCategory$: Observable<AllocationCategory>;
  isAllocationPendingDisplayed$: Observable<boolean>;
  isCancelAllocationSuccessDisplayed$: Observable<boolean>;
  isSeniorAdmin$: Observable<boolean>;

  private readonly unsubscribe$: Subject<void> = new Subject();

  constructor(
    private store: Store,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.store.dispatch(loadRequestAllocationData());
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(isPendingAllocationsRequested());

    this.allocationYears$ = this.store.select(allocationYears);
    this.selectedAllocationYear$ = this.store.select(selectedAllocationYear);
    this.selectedAllocationCategory$ = this.store.select(
      selectedAllocationCategory
    );

    this.isSeniorAdmin$ = this.store.select(isSeniorAdmin);

    // Display the Allocation Cancelled Successfully dialog if it is not loading and if a result has been returned
    this.isCancelAllocationSuccessDisplayed$ = this.store.select(
      isCancelAllocationSuccessAndPendingAllocationsNotLoading
    );

    // Display pending alloccations warning if there are pending allocations and the cancel allocation success
    // dialog is not displayed
    this.isAllocationPendingDisplayed$ = this.store.select(
      isAllocationPendingAndNoNotification
    );

    // This subscription handled where the pending cancellation success message is displayed
    // If there is a success flag in the store, then the after the delay an action is dispatched
    // which removes the message
    this.store
      .select(cancelPendingAllocationsResult)
      .pipe(
        filter((res) => res.success),
        delay(PENDING_ALLOCATION_NOTIFICATION_DELAY),
        tap(() =>
          this.store.dispatch(cancelPendingAllocationsMessageTimeout())
        ),
        first(),
        takeUntil(this.unsubscribe$)
      )
      .subscribe();
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onSelectAllocationYear(year: number) {
    this.store.dispatch(selectAllocationYear({ year }));
  }

  onSelectAllocationCategory(category: AllocationCategory) {
    this.store.dispatch(selectAllocationCategory({ category }));
  }

  onCancelAllocations() {
    const goBackRoute = this.router.url;
    return this.router
      .navigate([pathCancelPendingAllocations], {
        skipLocationChange: true,
        relativeTo: this.route,
      })
      .then(() => this.store.dispatch(canGoBack({ goBackRoute })));
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
