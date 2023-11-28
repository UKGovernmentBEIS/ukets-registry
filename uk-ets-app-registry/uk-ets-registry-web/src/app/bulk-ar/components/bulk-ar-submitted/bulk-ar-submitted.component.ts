import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-bulk-ar-submitted',
  templateUrl: './bulk-ar-submitted.component.html',
})
export class BulkArSubmittedComponent {
  @Input()
  numberOfBulkArTasks: string;
}
