import { Component, Input } from '@angular/core';
import {
  AggregatedAllocation,
  ALLOCATION_STATUS_LABELS,
  AllocationStatus,
  AnnualAllocation,
} from '@shared/model/account/account-allocation';
import { AccountActions } from '@account-management/account/account-details';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { getUrlIdentifier } from '@shared/shared.util';
import { ReturnExcessAllocationType } from '@shared/model/allocation';

@Component({
  selector: 'app-allocation-table',
  templateUrl: './allocation-table.component.html',
  styleUrls: ['./allocation-table.component.scss'],
})
export class AllocationTableComponent {
  @Input() allocation: AggregatedAllocation;
  @Input() ariaDescription: string;
  @Input() allocationType: string;
  @Input() canRequestTransaction: boolean;

  allocationStatusLabels = ALLOCATION_STATUS_LABELS;
  allocationStatusAllowed = AllocationStatus.ALLOWED;

  constructor(private store: Store, private activatedRoute: ActivatedRoute) {}

  goToReturnExcessTransaction(annual: AnnualAllocation) {
    const allocationType = this.allocationType as ReturnExcessAllocationType;
    this.store.dispatch(
      AccountActions.prepareTransactionStateForReturnOfExcess({
        routeSnapshotUrl: getUrlIdentifier(
          this.activatedRoute.snapshot['_routerState'].url
        ),
        allocationYear: annual.year,
        allocationType: allocationType,
        excessAmountPerAllocationAccount: {
          returnToAllocationAccountAmount: Math.abs(annual.remaining),
          returnToNewEntrantsReserveAccount: 0,
        },
      })
    );
  }

  getRemainingToBeAllocated(annuals: AnnualAllocation[]) {
    return annuals.reduce(function (r, aa) {
      return aa.remaining > 0 ? r + aa.remaining : r;
    }, 0);
  }

  getRemainingToBeReturned(annuals: AnnualAllocation[]) {
    return annuals.reduce(function (r, aa) {
      return aa.remaining < 0 ? r + Math.abs(aa.remaining) : r;
    }, 0);
  }
}
