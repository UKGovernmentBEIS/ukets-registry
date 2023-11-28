import { Component, Input, OnInit } from '@angular/core';
import { BusinessCheckResult } from '@shared/model/transaction';

@Component({
  selector: 'app-allocation-request-submitted',
  templateUrl: './allocation-request-submitted.component.html'
})
export class AllocationRequestSubmittedComponent {
  @Input() businessCheckResult: BusinessCheckResult;
}
