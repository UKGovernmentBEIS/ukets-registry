import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { clearAccountAllocation } from '@account-management/account/account-details/account.actions';
import { Store } from '@ngrx/store';
import { AccountAllocation } from '@shared/model/account/account-allocation';
import { Observable } from 'rxjs';
import {
  selectAccountAllocation,
  selectLastYear,
} from '@account-management/account/account-details/account.selector';
import { clearErrors } from '@shared/shared.action';
import { OperatorType } from '@shared/model/account';
import { AllocationType } from '@shared/model/allocation';

@Component({
  selector: 'app-account-allocation',
  templateUrl: './account-allocation.component.html',
})
export class AccountAllocationComponent implements OnInit, OnDestroy {
  @Input() accountId: string;
  @Input() operatorType: OperatorType;
  @Input() canRequestUpdate: boolean;
  @Output() readonly openHistoryAndComments = new EventEmitter<void>();
  accountAllocation$: Observable<AccountAllocation>;
  lastYear$: Observable<number>;

  constructor(private store: Store) {}
  readonly allocationTableType = AllocationType;
  operatorTypes = OperatorType;

  ngOnInit() {
    this.accountAllocation$ = this.store.select(selectAccountAllocation);
    this.lastYear$ = this.store.select(selectLastYear);
  }

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
