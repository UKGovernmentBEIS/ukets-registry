import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-my-profile',
  template: `
    <a
      [routerLink]="['/user-details', 'my-profile']"
      class="govuk-header__link uk_ets_sign_in_out_link govuk-header__link--sign-custom"
      >My Profile</a
    >
  `,
  styles: []
})
export class MyProfileComponent {
  @Input() urid: string;
}
