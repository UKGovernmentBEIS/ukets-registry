import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-email-sent',
  templateUrl: './email-sent.component.html',
  styles: []
})
export class EmailSentComponent {
  @Input()
  emailAddress: string;
}
