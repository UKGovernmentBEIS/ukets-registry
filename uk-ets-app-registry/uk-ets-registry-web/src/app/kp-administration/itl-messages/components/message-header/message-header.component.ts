import { Component, Input } from '@angular/core';
import { MessageDetails } from '@kp-administration/itl-messages/model';

@Component({
  selector: 'app-message-header',
  templateUrl: './message-header.component.html',
  styleUrls: ['../../../../shared/sub-headers/styles/sub-header.scss'],
})
export class MessageHeaderComponent {
  @Input() messageDetails: MessageDetails;
}
