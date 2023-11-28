import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AllocationRequestTaskDetails } from '@task-management/model';
import {
  ALLOCATION_TYPE_LABELS,
  AllocationCategory,
} from '@shared/model/allocation';

@Component({
  selector: 'app-allocation-request-task-details',
  templateUrl: './allocation-request-task-details.component.html',
  styleUrls: ['./allocation-request-task-details.component.scss'],
})
export class AllocationRequestTaskDetailsComponent implements OnInit {
  @Input() taskDetails: AllocationRequestTaskDetails;
  @Output() readonly downloadAllocationFile = new EventEmitter();

  typeLabel: string;
  types: string[] = [];

  ngOnInit() {
    this.types = Object.keys(this.taskDetails.allocationOverview.rows);
    this.typeLabel = this.getOperatorsInstallationsLabel();
  }

  getLabel(type: string) {
    return ALLOCATION_TYPE_LABELS[type]?.label;
  }

  onDownloadFile() {
    this.downloadAllocationFile.emit();
  }

  getOperatorsInstallationsLabel() {
    if (
      this.taskDetails.allocationOverview.category ===
      AllocationCategory.AircraftOperator
    ) {
      return 'operators';
    } else if (
      this.taskDetails.allocationOverview.category ===
      AllocationCategory.Installation
    ) {
      return 'installations';
    } else {
      return 'operators/installations';
    }
  }
}
