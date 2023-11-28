import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-submit-success-change-description',
  templateUrl: './submit-success-change-description.component.html',
})
export class SubmitSuccessChangeDescriptionComponent {
  @Input() message: string;
  @Input() backLinkMessage: string;
  @Input() backPath: string;
}
