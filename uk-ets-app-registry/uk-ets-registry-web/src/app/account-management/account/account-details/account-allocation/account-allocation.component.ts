import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { Store } from '@ngrx/store';
import {
  selectAccountAllocation,
  selectGroupedAllocationOverview,
  selectLastYear,
} from '@account-management/account/account-details/account.selector';
import { clearErrors } from '@shared/shared.action';
import { OperatorType } from '@shared/model/account';
import { AllocationType } from '@shared/model/allocation';

@Component({
  selector: 'app-account-allocation',
  templateUrl: './account-allocation.component.html',
})
export class AccountAllocationComponent implements OnDestroy {
  @Input() accountId: string;
  @Input() operatorType: OperatorType;
  @Input() canRequestUpdate: boolean;
  @Output() readonly openHistoryAndComments = new EventEmitter<void>();

  readonly accountAllocation$ = this.store.select(selectAccountAllocation);
  readonly groupedAllocationOverview$ = this.store.select(
    selectGroupedAllocationOverview
  );
  readonly lastYear$ = this.store.select(selectLastYear);

  readonly AllocationType = AllocationType;

  constructor(private readonly store: Store) {}

  getAllocationTableType() {
    if (this.operatorType === OperatorType.INSTALLATION) {
      return AllocationType.NAT;
    }
    return AllocationType.NAVAT;
  }

  navToHistoryAndComments() {
    this.openHistoryAndComments.emit();
  }

  ngOnDestroy(): void {
    this.store.dispatch(clearErrors());
  }
}
