import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AllocationCategory } from '@registry-web/shared/model/allocation';

@Component({
  selector: 'app-check-allocation-request',
  templateUrl: './check-allocation-request.component.html',
})
export class CheckAllocationRequestComponent {
  @Input() selectedAllocationYear: number;
  @Input() selectedAllocationCategory: AllocationCategory;
  @Output() readonly submitRequest = new EventEmitter<void>();
  @Output() readonly downloadAllocationFile = new EventEmitter();
}
