import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-reset-password-success',
  templateUrl: './reset-password-success.component.html',
  styles: []
})
export class ResetPasswordSuccessComponent {
  @Input()
  emailAddress: string;
}
