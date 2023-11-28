import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';

import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { TRUSTED_ACCOUNTS_TYPE_OPTIONS } from '@registry-web/shared/model/account/trusted-account';

@Component({
  selector: 'app-search-trusted-accounts-form',
  templateUrl: './search-trusted-accounts-form.component.html',
})
export class SearchTrustedAccountsFormComponent
  extends UkFormComponent
  implements OnInit
{
  trustedAccountType = TRUSTED_ACCOUNTS_TYPE_OPTIONS;
  @Output() readonly search = new EventEmitter<any>();
  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  onClear() {
    this.formGroup.reset();
  }

  protected getFormModel() {
    return {
      accountNumber: [''],
      accountNameOrDescription: ['', Validators.minLength(3)],
      trustedAccountType: [''],
    };
  }

  protected doSubmit() {
    this.search.emit(this.formGroup.value);
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      accountNameOrDescription: {
        minlength:
          'Enter at least 3 characters in the "Account name or description"',
      },
    };
  }
}
