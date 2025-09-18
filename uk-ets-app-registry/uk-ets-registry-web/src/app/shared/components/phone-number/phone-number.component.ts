import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-shared-phone-number',
  styles: [
    `
      :host {
        display: block;
      }
    `,
  ],
  templateUrl: './phone-number.component.html',
})
export class PhoneNumberComponent {
  @Input()
  countryCode: string;
  @Input()
  phoneNumber: string;
}
