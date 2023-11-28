import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-notification-renderer',
  templateUrl: './notification-renderer.component.html',
  styleUrls: ['./notification-renderer.component.css'],
})
export class NotificationRendererComponent {
  @Input() content: string;
}
