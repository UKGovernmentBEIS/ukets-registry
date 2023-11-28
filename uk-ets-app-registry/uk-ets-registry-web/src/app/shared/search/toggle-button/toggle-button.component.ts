import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-show-hide-toggle-button',
  templateUrl: './toggle-button.component.html',
})
export class ToggleButtonComponent {
  @Input() displayable: boolean;
  @Input() trueStateLabel: string;
  @Input() falseStateLabel: string;
  @Input() condition: boolean;
  @Output() readonly toggle = new EventEmitter<boolean>();
}
