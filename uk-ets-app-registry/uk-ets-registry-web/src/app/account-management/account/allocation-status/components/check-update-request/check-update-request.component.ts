import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UpdateAllocationStatusRequest } from '@allocation-status/model';
import { ALLOCATION_STATUS_LABELS } from '@shared/model/account';

@Component({
  selector: 'app-check-update-request',
  templateUrl: './check-update-request.component.html',
  styleUrls: ['./check-update-request.component.scss'],
})
export class CheckUpdateRequestComponent implements OnInit {
  @Input() updateRequest: UpdateAllocationStatusRequest;
  @Output() readonly changeUpdate = new EventEmitter();
  @Output() readonly applyButtonClicked = new EventEmitter();

  updateRequestSnapshot: UpdateAllocationStatusRequest;

  allocationStatusLabels = ALLOCATION_STATUS_LABELS;

  onChangeClick() {
    this.changeUpdate.emit();
  }

  onApplyButtonClicked() {
    this.applyButtonClicked.emit();
  }

  ngOnInit(): void {
    this.updateRequestSnapshot = this.updateRequest;
  }
}
