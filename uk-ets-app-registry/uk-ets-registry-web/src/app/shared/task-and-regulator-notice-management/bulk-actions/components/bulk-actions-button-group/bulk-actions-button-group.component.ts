import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-bulk-actions-button-group',
  templateUrl: './bulk-actions-button-group.component.html',
  standalone: true,
  imports: [],
})
export class BulkActionsButtonGroupComponent {
  @Input() disabled: boolean;
  @Input() displayable: boolean;
  @Output() readonly claim = new EventEmitter();
  @Output() readonly assign = new EventEmitter();

  onClaim() {
    this.claim.emit();
  }

  onAssign() {
    this.assign.emit();
  }
}
