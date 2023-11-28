import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EnrolledUser } from '@authority-setting/model';

@Component({
  selector: 'app-check-authority-user',
  templateUrl: './check-authority-user.component.html',
})
export class CheckAuthorityUserComponent {
  @Input() enrolledUser: EnrolledUser;
  @Output() readonly setUserAsAuthority = new EventEmitter<string>();
  @Output() readonly removeUserFromAuthorityUsers = new EventEmitter<string>();
  @Output() readonly changeUser = new EventEmitter();

  onSetUserAsAuthority() {
    this.setUserAsAuthority.emit(this.enrolledUser.userId);
  }

  onRemoveUserFromAuthorityUsers() {
    this.removeUserFromAuthorityUsers.emit(this.enrolledUser.userId);
  }

  onChangeClick() {
    this.changeUser.emit();
  }
}
