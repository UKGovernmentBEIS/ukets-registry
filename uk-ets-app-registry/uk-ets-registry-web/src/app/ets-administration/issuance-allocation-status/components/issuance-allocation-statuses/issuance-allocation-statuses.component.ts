import { Component, Input } from '@angular/core';
import { AllowanceReport } from '../../model';

@Component({
  selector: 'app-issuance-allocation-statuses',
  templateUrl: './issuance-allocation-statuses.component.html'
})
export class IssuanceAllocationStatusesComponent {
  @Input()
  issuanceAllocationStatuses: AllowanceReport[];
}
