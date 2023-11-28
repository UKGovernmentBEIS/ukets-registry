import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import * as RequestAllocationActions from '@request-allocation/actions';
import { isCancelPendingAllocationsLoading } from '@request-allocation/reducers';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-cancel-pending-allocations',
  templateUrl: './cancel-pending-allocations.component.html',
})
export class CancelPendingAllocationsComponent implements OnInit, OnDestroy {
  private isLoading = false;
  private readonly unsubscribe$: Subject<void> = new Subject();

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store
      .select(isCancelPendingAllocationsLoading)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe((isLoading) => {
        this.isLoading = isLoading;
      });
  }

  onCancelPendingAllocations() {
    if (!this.isLoading) {
      this.store.dispatch(
        RequestAllocationActions.cancelPendingAllocationsRequested()
      );
    }
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
