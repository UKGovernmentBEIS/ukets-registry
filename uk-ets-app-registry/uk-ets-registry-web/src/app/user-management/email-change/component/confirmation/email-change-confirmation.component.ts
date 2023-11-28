import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EmailChangeConfirmation } from '@email-change/model';

@Component({
  selector: 'app-email-change-confirmation',
  templateUrl: './email-change-confirmation.component.html',
})
export class EmailChangeConfirmationComponent {
  @Input() confirmation: EmailChangeConfirmation;
  @Output() readonly restart = new EventEmitter();
  @Output() readonly backToMyProfile = new EventEmitter();
  @Output() readonly signIn = new EventEmitter();

  restartEmailChange() {
    this.restart.emit();
  }

  onClickBack() {
    this.backToMyProfile.emit();
  }

  onSignIn() {
    this.signIn.emit();
  }
}
