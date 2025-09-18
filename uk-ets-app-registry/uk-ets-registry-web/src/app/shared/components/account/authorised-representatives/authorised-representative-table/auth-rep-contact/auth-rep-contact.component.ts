import { Component, Input } from '@angular/core';
import { Contact } from '@shared/model/contact';

@Component({
  selector: 'app-shared-authorised-representative-contact',
  templateUrl: './auth-rep-contact.component.html',
})
export class AuthRepContactComponent {
  @Input() contact: Contact;
  @Input() firstName: string;
  @Input() lastName: string;

  moreInfo = false;
}
