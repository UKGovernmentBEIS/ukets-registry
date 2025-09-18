import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  USER_STATUS_OPTIONS,
  USER_ROLE_OPTIONS,
  UserSearchCriteria,
} from '../user-list.model';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';

@Component({
  selector: 'app-search-users-form',
  templateUrl: './search-users-form.component.html',
})
export class SearchUsersFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() storedCriteria: UserSearchCriteria;
  @Output() readonly search = new EventEmitter<UserSearchCriteria>();
  @Output() readonly submitClick = new EventEmitter<null>();

  userStatusOptions = USER_STATUS_OPTIONS;
  userRoleOptions = USER_ROLE_OPTIONS;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel() {
    return {
      nameOrUserId: ['', Validators.minLength(3)],
      status: ['ALL_EXCEPT_DEACTIVATED'],
      email: ['', Validators.minLength(3)],
      lastSignInFrom: [null],
      lastSignInTo: [null],
      role: [''],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      nameOrUserId: {
        minlength: 'Enter at least 3 characters in the "Name or user ID"',
      },
      email: {
        minlength: 'Enter at least 3 characters in the "User email address"',
      },
      lastSignInFrom: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      lastSignInTo: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
    };
  }

  ngOnInit() {
    super.ngOnInit();
    if (this.storedCriteria) {
      this.formGroup.patchValue(this.storedCriteria);
    }
    this.search.emit(this.formGroup.value);
  }

  protected doSubmit() {
    this.submitClick.emit();
    this.search.emit(this.formGroup.value);
  }

  onClear() {
    this.formGroup.reset();
    this.doSubmit();
  }
}
