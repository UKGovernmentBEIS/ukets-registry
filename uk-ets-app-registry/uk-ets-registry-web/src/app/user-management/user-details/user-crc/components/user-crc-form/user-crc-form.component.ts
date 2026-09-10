import {
  Component,
  computed,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  viewChild,
} from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { CRC_LABELS } from '@user-management/user-details/model/crc.model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { CrcDetails, UserCrcUpdateRequest } from '@user-crc/model';
import { UkDate } from '@shared/model/uk-date';
import { takeUntil } from 'rxjs/operators';
import { emptyProp } from '@registry-web/shared/shared.util';

@Component({
  selector: 'app-user-crc-form',
  templateUrl: './user-crc-form.component.html',
})
export class UserCrcFormComponent extends UkFormComponent implements OnInit {
  @Input()
  crc: boolean;
  @Output()
  readonly crcDetailsOutput = new EventEmitter<UserCrcUpdateRequest>();

  _crcDetails: CrcDetails;

  constructor() {
    super();
  }

  @Input()
  set crcDetails(value: CrcDetails) {
    this._crcDetails = value;
  }

  ngOnInit() {
    super.ngOnInit();
    //Init components enable/disable state based on crcControl value
    if (this.crc === true) {
      this.crcIssuanceDateControl.enable();
      this.formGroup.get('crcDetails').enable();
    } else {
      this.crcIssuanceDateControl.disable();
      this.formGroup.get('crcDetails').disable();
    }

    //Subscribe for changes
    this.crcControl.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((crc) => {
        if (crc === true) {
          this.crcIssuanceDateControl.enable();
          this.formGroup.get('crcDetails').enable();
          this.formGroup.get('crcDetails').get('crcIssuanceDate').enable();
        } else {
          this.crcIssuanceDateControl.disable();
          this.formGroup.get('crcDetails').disable();
          this.formGroup.get('crcDetails').get('crcIssuanceDate').disable();
        }
        this.formGroup.markAsUntouched();
        this.formGroup.updateValueAndValidity();
        this.errorDetails.emit([]);
      });
  }

  protected getFormModel(): any {
    return {
      crc: [
        this.crc ?? null,
        {
          validators: [Validators.required],
          updateOn: 'change',
        },
      ],
      crcDetails: this.formBuilder.group({
        crcIssuanceDate: [
          this._crcDetails
            ? this._crcDetails.crcIssuanceDate
            : { day: null, month: null, year: null },
        ],
      }),
    };
  }
  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      crc: {
        required: 'CRC attribute is required',
      },
      crcIssuanceDate: {
        tooYoung: 'The crc issuance date is invalid',
        tooOld: 'The crc issuance date is invalid',
        missingField: 'Enter a complete date',
        invalidInput: 'Enter a valid date',
        invalidDate: 'The date is invalid',
        invalidDay: 'Enter a valid day',
        invalidMonth: 'Enter a valid month',
      },
    };
  }

  onContinue() {
    this.onSubmit();
  }

  doSubmit() {
    const outputModel: UserCrcUpdateRequest = {
      crc: null,
      crcIssuanceDate: null,
    };
    outputModel.crc = this.crcControl.value;
    if (outputModel.crc === true) {
      if (!this.crcIssuanceDateControl.value) {
        outputModel.crcIssuanceDate = null;
      } else {
        outputModel.crcIssuanceDate = this.convertToISOString(
          this.crcIssuanceDateControl.value
        );
      }
    }
    this.crcDetailsOutput.emit(outputModel);
  }

  convertToISOString(ukDate: UkDate): string {
    if (!ukDate || emptyProp(ukDate)) {
      return null;
    }
    const year = Number(ukDate.year);
    const month = Number(ukDate.month);
    const day = Number(ukDate.day);
    if (!year || !month || !day) {
      return null;
    }
    const utcDate = new Date(Date.UTC(year, month - 1, day, 0, 0, 0));
    return utcDate.toISOString();
  }

  readonly crcRadioGroup = computed<FormRadioGroupInfo>(() => ({
    key: 'crc',
    options: [
      {
        label: CRC_LABELS['true'].label,
        value: true,
        enabled: true,
        conditionalTemplate: this.crcDetailsTemplate(),
      },
      {
        label: CRC_LABELS['false'].label,
        value: false,
        enabled: true,
      },
    ],
  }));

  private readonly crcDetailsTemplate = viewChild.required(
    'crcDetailsTemplate',
    { read: TemplateRef }
  );

  //Form controls getters
  private get crcControl() {
    return this.formGroup.get('crc') as FormControl<boolean>;
  }

  private get crcIssuanceDateControl() {
    return this.formGroup
      .get('crcDetails')
      .get('crcIssuanceDate') as FormControl<UkDate>;
  }

  showCrcDetailsError(): boolean {
    return (
      this.formGroup.touched &&
      this.crcControl.value === true &&
      !!this.formGroup.get('crcDetails').errors &&
      !!this.validationErrorMessage.crcDetails
    );
  }
}
