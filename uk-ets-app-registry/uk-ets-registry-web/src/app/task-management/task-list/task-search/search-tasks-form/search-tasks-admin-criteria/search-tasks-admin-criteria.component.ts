import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
} from '@angular/forms';
import { TaskSearchCriteria, TaskType } from '@task-management/model';
import {
  ACCOUNT_TYPE_OPTIONS,
  ALLOCATION_CATEGORY_OPTIONS,
  TASK_OUTCOME_OPTIONS,
  TASK_STATUS_OPTIONS,
  USER_ROLE_OPTIONS,
  USER_TASK_OPTIONS,
} from '../search-tasks-form.model';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { UkValidationMessageHandler } from '@registry-web/shared/validation';
import { Option } from '@registry-web/shared/form-controls/uk-select-input/uk-select.model';

@Component({
  selector: 'app-search-tasks-admin-criteria',
  templateUrl: './search-tasks-admin-criteria.component.html',
  styles: [],
})
export class SearchTasksAdminCriteriaComponent
  implements OnInit, AfterViewInit
{
  form: UntypedFormGroup;
  @Input() storedCriteria: TaskSearchCriteria;
  @Input() taskTypeOptions: TaskType[];
  @Input() allocationYearOptions: Option[];
  @Input() showAdvancedSearch: boolean;

  @Output() readonly search = new EventEmitter<TaskSearchCriteria>();
  @Output() readonly advancedSearch = new EventEmitter<boolean>();
  @Output() readonly submitClick = new EventEmitter<null>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  @ViewChild('summary') summary: ElementRef;

  taskStatusOptions = TASK_STATUS_OPTIONS;
  taskOutcomeOptions = TASK_OUTCOME_OPTIONS;
  accountTypeOptions = ACCOUNT_TYPE_OPTIONS;
  userTaskOptions = USER_TASK_OPTIONS;
  userRoleOptions = USER_ROLE_OPTIONS;
  allocationCategoryOptions = ALLOCATION_CATEGORY_OPTIONS;

  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  constructor(private formBuilder: UntypedFormBuilder) {}

  ngOnInit() {
    this.initForm();
    this.search.emit(this.form.value);

    this.validationMessages = {
      claimedOnFrom: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      claimedOnTo: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      createdOnFrom: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      createdOnTo: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      completedOnFrom: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      completedOnTo: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
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

  onReset() {
    this.form.reset();
    this.form.patchValue({
      excludeUserTasks: true,
      taskStatus: 'OPEN',
      claimedBy: 'ALL_EXCEPT_AUTHORIZED_REPRESENTATIVES',
    });
    this.submitClick.emit();
    this.search.emit(this.form.value);
  }

  private initForm() {
    this.form = this.formBuilder.group({
      accountNumber: [],
      accountHolder: [],
      taskStatus: ['OPEN'],
      claimantName: [],
      taskType: [],
      requestId: [],
      transactionId: [],
      taskOutcome: [],
      initiatorName: [],
      accountType: [],
      excludeUserTasks: [true],
      initiatedBy: [],
      claimedBy: ['ALL_EXCEPT_AUTHORIZED_REPRESENTATIVES'],
      allocationCategory: [],
      allocationYear: [],
      nameOrUserId: [],
      claimedOnFrom: [],
      claimedOnTo: [],
      createdOnFrom: [],
      createdOnTo: [],
      completedOnFrom: [],
      completedOnTo: [],
    });
  }

  ngAfterViewInit(): void {
    if (this.storedCriteria) {
      this.form.patchValue(this.storedCriteria);
      this.search.emit(this.form.value);
    }
  }

  toggleAdvancedSearch(): void {
    const elm = this.summary.nativeElement as HTMLElement;
    setTimeout(() => {
      const showAdvancedSearch = elm.parentElement.attributes['open']
        ? true
        : false;
      this.advancedSearch.emit(showAdvancedSearch);
    });
  }
}
