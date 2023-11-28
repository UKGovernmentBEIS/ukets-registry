import {
  Component,
  ContentChild,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';

import { ErrorDetail } from '@shared/error-summary';

import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UkValidationMessageHandler } from '@shared/validation';
import { AccountType } from '@shared/model/account';

@Component({
  selector: 'app-account-type',
  templateUrl: './account-type.component.html',
})
export class AccountTypeComponent implements OnInit {
  @Output() readonly selectedAccountType = new EventEmitter<AccountType>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();
  @Input() isAdmin: boolean;
  @Input() helpdeskEmail: string;
  @ViewChild('infoTemplate', { static: true })
  infoTemplate: TemplateRef<ElementRef>;

  accountTypeFormGroup: UntypedFormGroup;

  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  accountTypeGroupInfo: FormRadioGroupInfo;
  constructor(private formBuilder: UntypedFormBuilder) {}

  ngOnInit() {
    this.accountTypeGroupInfo = {
      radioGroupHeading: 'Choose an account type',
      radioGroupHeadingCaption:
        'Request to open an Emissions Trading Registry account',
      key: 'accountType',
      subGroups: [
        {
          heading: 'UK Emission Trading Scheme (UK ETS)',
          options: [
            {
              label: 'Operator Holding Account',
              value: AccountType.OPERATOR_HOLDING_ACCOUNT,
              hint: 'Used for compliance and trading UK ETS allowances',
              enabled: this.isAdmin,
              infoTemplate: !this.isAdmin ? this.infoTemplate : null,
            },
            {
              label: 'Aircraft Operator Holding Account',
              value: AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
              hint: 'Used for compliance and trading UK ETS allowances',
              enabled: this.isAdmin,
              infoTemplate: !this.isAdmin ? this.infoTemplate : null,
            },
            {
              label: 'Trading Account',
              value: AccountType.TRADING_ACCOUNT,
              hint: 'Used for holding and trading UK ETS allowances',
              enabled: true,
            },
          ],
        },
        {
          heading: 'Kyoto Protocol',
          options: [
            {
              label: 'Person Holding Account',
              value: AccountType.PERSON_HOLDING_ACCOUNT,
              hint: 'Used for holding and trading international units such as CERs',
              enabled: true,
            },
          ],
        },
      ],
    };

    this.accountTypeFormGroup = this.formBuilder.group(
      {
        accountType: ['', Validators.required],
      },
      { updateOn: 'submit' }
    );

    this.validationMessages = {
      accountType: {
        required: 'Choose an account type',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  onContinue() {
    this.accountTypeFormGroup.markAllAsTouched();
    if (this.accountTypeFormGroup.valid) {
      this.selectedAccountType.emit(
        this.accountTypeFormGroup.get('accountType').value
      );
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.accountTypeFormGroup
      );
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }

  showErrors(): boolean {
    return (
      this.accountTypeFormGroup.invalid && this.accountTypeFormGroup.touched
    );
  }
}
