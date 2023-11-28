import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-allocation-table-submitted',
  templateUrl: './allocation-table-submitted.component.html'
})
export class AllocationTableSubmittedComponent {
  @Input()
  requestId: string;
}
