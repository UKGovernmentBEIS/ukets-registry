import { Component, output } from '@angular/core';
import { ReactiveFormsModule, Validators } from '@angular/forms';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import { AccountClaimDTO } from '@registry-web/shared/model/account';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  selector: 'app-claim-account-form',
  templateUrl: './claim-account-form.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, SharedModule],
})
export class ClaimAccountFormComponent extends UkFormComponent {
  readonly formSubmit = output<AccountClaimDTO>();

  protected getFormModel() {
    return {
      accountClaimCode: ['', [Validators.required]],
      registryId: ['', [Validators.required]],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      accountClaimCode: {
        required: 'Enter the claim account code.',
      },
      registryId: {
        required: 'Enter the METS Registry ID.',
      },
    };
  }

  protected doSubmit() {
    this.formSubmit.emit(this.formGroup.value);
  }
}
