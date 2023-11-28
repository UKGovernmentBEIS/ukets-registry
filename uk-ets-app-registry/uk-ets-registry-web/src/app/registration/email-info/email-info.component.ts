import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-email-info',
  templateUrl: './email-info.component.html',
  styles: [],
})
export class EmailInfoComponent {
  @Input() cookiesAccepted: boolean;
  @Output() readonly navigationEmitter = new EventEmitter<string>();

  navigateToEmailAddress() {
    this.navigationEmitter.emit('/registration/emailAddress');
  }
}
