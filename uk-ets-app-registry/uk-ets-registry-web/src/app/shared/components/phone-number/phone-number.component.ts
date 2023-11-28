import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-shared-phone-number',
  templateUrl: './phone-number.component.html'
})
export class PhoneNumberComponent {
  @Input()
  countryCode: string;
  @Input()
  phoneNumber: string;
}
