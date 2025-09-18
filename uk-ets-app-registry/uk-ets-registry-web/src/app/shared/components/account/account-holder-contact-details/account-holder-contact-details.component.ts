import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { AccountHolderContact } from '@shared/model/account';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { UkRegistryValidators } from '@shared/validation';
import { selectIsOHAOrAOHA } from '@account-management/account/account-details/account.selector';
import { Store } from '@ngrx/store';
import { take } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { selectIsMOHA } from '@account-opening/account-opening.selector';

@Component({
  selector: 'app-account-holder-contact-details',
  templateUrl: './account-holder-contact-details.component.html',
})
export class AccountHolderContactDetailsComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() isAHUpdateWizard = false;
  @Input() useUpdateLabel = true;
  @Input() accountHolderContact: AccountHolderContact;
  @Input() contactType: string;
  @Output() readonly outputUser = new EventEmitter<AccountHolderContact>();

  title: string;

  isMOHA: boolean;

  constructor(
    protected formBuilder: UntypedFormBuilder,
    private store: Store
  ) {
    super();
  }

  ngOnInit() {
    this.store
      .select(selectIsMOHA)
      .pipe(take(1))
      .subscribe((type) => {
        this.isMOHA = type;
      });
    super.ngOnInit();
    this.initFormValues();
  }

  initFormValues() {
    this.formGroup.get('details').get('firstName').patchValue('Primary');
    this.formGroup.get('details').get('lastName').patchValue('Contact');
    this.formGroup.get('details').get('isOverEighteen').patchValue(true);
  }

  protected getFormModel() {
    return {
      details: this.formBuilder.group({
        firstName: [
          '',
          [
            Validators.required,
            UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
          ],
        ],
        lastName: [
          '',
          [
            Validators.required,
            UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
          ],
        ],
        aka: [''],
        isOverEighteen: [false, Validators.requiredTrue],
      }),
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      firstName: {
        required: 'Enter the first name.',
      },
      lastName: {
        required: 'Enter the last name.',
      },
      isOverEighteen: {
        required: `The ${this.getContactLabel()} must be aged 18 or over.`,
      },
    };
  }

  getSubtitle(): string {
    if (this.isAHUpdateWizard && this.useUpdateLabel) {
      return `Update the ${this.getContactLabel()} details`;
    } else {
      return `Add the ${this.getContactLabel()} details`;
    }
  }

  protected doSubmit() {
    const updateObject = new AccountHolderContact();
    updateObject.details = this.formGroup.get('details').value;
    this.outputUser.emit(updateObject);
  }

  getContactLabel() {
    return this.contactType === ContactType.PRIMARY
      ? 'Primary Contact'
      : 'alternative Primary Contact';
  }

  getAgeLabel() {
    return `I confirm that the ${this.getContactLabel()} is aged 18 or over`;
  }
}
