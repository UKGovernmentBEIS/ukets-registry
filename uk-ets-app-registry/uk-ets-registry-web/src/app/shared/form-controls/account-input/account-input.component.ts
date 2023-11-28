import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-account-input',
  templateUrl: './account-input.component.html',
  styleUrls: ['./account-input.component.scss']
})
// TODO This component should be implemented as form control and not just a shared component
export class AccountInputComponent {
  @Input() titleToDisplay: string;
  @Input() subTitleToDisplay: string;
  @Input() countryCode: string;
  @Input() accountType: string;
  @Input() accountId: string;
  @Input() period: string;
  @Input() checkDigits: string;
}
