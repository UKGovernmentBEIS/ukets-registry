import {
  Component,
  computed,
  inject,
  input,
  OnInit,
  output,
} from '@angular/core';
import {
  ReactiveFormsModule,
  UntypedFormBuilder,
  ValidationErrors,
} from '@angular/forms';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { ErrorDetail } from '@shared/error-summary';
import { UkValidationMessageHandler } from '@shared/validation';
import { SharedModule } from '@shared/shared.module';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { RegulatorNoticeSearchCriteria } from '@shared/task-and-regulator-notice-management/model';
import { REGULATOR_NOTICE_TASK_STATUS_OPTIONS } from '@regulator-notice-management/list/regulator-notice-list.const';

@Component({
  selector: 'app-regulator-notice-list-form',
  templateUrl: './regulator-notice-list-form.component.html',
  standalone: true,
  imports: [SharedModule, NgbTypeaheadModule, ReactiveFormsModule],
})
export class RegulatorNoticeListFormComponent implements OnInit {
  private readonly formBuilder = inject(UntypedFormBuilder);

  readonly storedCriteria = input.required<RegulatorNoticeSearchCriteria>();
  readonly processTypesList = input.required<string[]>();
  readonly search = output<RegulatorNoticeSearchCriteria>();
  readonly advancedSearch = output<boolean>();
  readonly submitClick = output<void>();
  readonly errorDetails = output<ErrorDetail[]>();

  readonly taskStatusOptions = REGULATOR_NOTICE_TASK_STATUS_OPTIONS;
  readonly processTypeOptions = computed<Option[]>(() => [
    { label: '', value: null },
    ...this.processTypesList().map((processType) => ({
      label: processType,
      value: processType,
    })),
  ]);
  readonly form = this.formBuilder.group({
    accountNumber: [],
    accountHolderId: [],
    processType: [],
    taskStatus: [],
    operatorId: [],
    permitOrMonitoringPlanIdentifier: [],
    claimantName: [],
  });

  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } } = {};
  private genericValidator: UkValidationMessageHandler =
    new UkValidationMessageHandler(this.validationMessages);

  ngOnInit() {
    if (this.storedCriteria()) {
      this.form.patchValue(this.storedCriteria());
    }
    this.search.emit(this.form.value);
  }

  onSearch() {
    this.form.updateValueAndValidity();

    if (this.form.valid) {
      this.submitClick.emit();
      this.search.emit(this.form.value);
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.form
      );
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }

  onClear() {
    this.form.reset();
    this.submitClick.emit();
    this.search.emit(this.form.value);
  }
}
