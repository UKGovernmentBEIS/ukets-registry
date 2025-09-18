import { Component, EventEmitter, Input, Output } from '@angular/core';
import { KeycloakUser } from '@shared/user/keycloak-user';
import { EnrolmentKey } from '@user-management/user-details/model';

import { ViewMode } from '@user-management/user-details/model/user-details.model';
import { userStatusMap } from '@shared/user';

@Component({
  selector: 'app-registration-details',
  templateUrl: './registration-details.component.html',
})
export class RegistrationDetailsComponent {
  @Input() user: KeycloakUser;
  @Input() isVisible: boolean;
  @Input() enrolmentKeyDetails: EnrolmentKey;
  @Input() currentViewMode: ViewMode;
  @Output() readonly emailChange = new EventEmitter<string>();
  @Output() readonly tokenChange = new EventEmitter();
  @Output() readonly passwordChange = new EventEmitter();

  readonly viewMode = ViewMode;

  userStatusMap = userStatusMap;

  getEmailMessage(): string {
    return this.currentViewMode === this.viewMode.MY_PROFILE
      ? 'Change your email address'
      : 'Change email address';
  }

  onClickEmailChange() {
    this.emailChange.emit(this.user.attributes.urid[0]);
  }

  onClickChangeToken() {
    this.tokenChange.emit();
  }

  onClickPasswordChange() {
    this.passwordChange.emit();
  }
}
