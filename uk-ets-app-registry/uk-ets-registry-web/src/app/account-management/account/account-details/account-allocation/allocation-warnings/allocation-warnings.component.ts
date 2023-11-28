import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AggregatedAllocation,
  AllocationStatus,
} from '@shared/model/account/account-allocation';

@Component({
  selector: 'app-allocation-warnings',
  templateUrl: './allocation-warnings.component.html',
})
export class AllocationWarningsComponent implements OnInit {
  @Input() natAllocation: AggregatedAllocation;
  @Input() nerAllocation: AggregatedAllocation;
  @Input() lastYear: number;
  @Output() readonly openHistoryAndComments = new EventEmitter<void>();
  withheld: boolean;
  overAllocated: boolean;
  pendingExcessAllocation: boolean;
  allocationsAfterLastYear: boolean;

  ngOnInit() {
    let overAllocatedNatAnnuals;
    let overAllocatedNerAnnuals;
    let withheldNatAnnuals;
    let withheldNerAnnuals;

    if (this.natAllocation != null) {
      overAllocatedNatAnnuals = this.natAllocation.annuals.filter(
        (annual) => annual.remaining < 0 && annual.eligibleForReturn === true
      );
      withheldNatAnnuals = this.natAllocation.annuals.filter(
        (annual) => annual.status === AllocationStatus.WITHHELD
      );
    }

    if (this.nerAllocation != null) {
      overAllocatedNerAnnuals = this.nerAllocation.annuals.filter(
        (annual) => annual.remaining < 0 && annual.eligibleForReturn === true
      );
      withheldNerAnnuals = this.nerAllocation.annuals.filter(
        (annual) => annual.status === AllocationStatus.WITHHELD
      );
    }

    if (overAllocatedNatAnnuals != null && overAllocatedNerAnnuals != null) {
      this.withheld =
        withheldNatAnnuals.length > 0 || withheldNerAnnuals.length > 0;
      this.overAllocated =
        overAllocatedNatAnnuals.length > 0 ||
        overAllocatedNerAnnuals.length > 0;
      this.pendingExcessAllocation =
        this.natAllocation.annuals.filter(
          (aa) => aa.remaining < 0 && aa.eligibleForReturn === false
        ).length > 0 ||
        this.nerAllocation.annuals.filter(
          (aa) => aa.remaining < 0 && aa.eligibleForReturn === false
        ).length > 0;
    } else if (
      overAllocatedNatAnnuals != null &&
      overAllocatedNerAnnuals == null
    ) {
      this.withheld = withheldNatAnnuals.length > 0;
      this.overAllocated = overAllocatedNatAnnuals.length > 0;
      this.pendingExcessAllocation =
        this.natAllocation.annuals.filter(
          (aa) => aa.remaining < 0 && aa.eligibleForReturn === false
        ).length > 0;
    } else {
      this.withheld = withheldNerAnnuals.length > 0;
      this.overAllocated = overAllocatedNerAnnuals.length > 0;
      this.pendingExcessAllocation =
        this.nerAllocation.annuals.filter(
          (aa) => aa.remaining < 0 && aa.eligibleForReturn === false
        ).length > 0;
    }

    if (this.lastYear) {
      const checkRemainingAfterLastYear = (annual) =>
        annual.year > this.lastYear && annual.allocated > 0;
      this.allocationsAfterLastYear =
        this.nerAllocation?.annuals.some(checkRemainingAfterLastYear) ||
        this.natAllocation?.annuals.some(checkRemainingAfterLastYear);
    } else {
      this.allocationsAfterLastYear = false;
    }
  }

  navToHistoryAndComments() {
    this.openHistoryAndComments.emit();
  }
}
