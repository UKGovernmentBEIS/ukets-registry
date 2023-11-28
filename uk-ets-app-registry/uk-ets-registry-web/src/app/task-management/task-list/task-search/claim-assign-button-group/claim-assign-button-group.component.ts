import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-claim-assign-button-group',
  templateUrl: './claim-assign-button-group.component.html',
})
export class ClaimAssignButtonGroupComponent {
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
