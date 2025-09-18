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
import {
  AccountType,
  TaskSearchCriteria,
  TaskType,
} from '@task-management/model';
import {
  TASK_OUTCOME_OPTIONS,
  TASK_STATUS_OPTIONS,
} from '../search-tasks-form.model';
import { UkValidationMessageHandler } from '@registry-web/shared/validation';
import { ErrorDetail } from '@registry-web/shared/error-summary';

@Component({
  selector: 'app-search-tasks-user-criteria',
  templateUrl: './search-tasks-user-criteria.component.html',
  styles: [],
})
export class SearchTasksUserCriteriaComponent implements OnInit, AfterViewInit {
  form: UntypedFormGroup;
  @Input() storedCriteria: TaskSearchCriteria;
  @Input() taskTypeOptions: TaskType[];
  @Input() accountTypeOptions: AccountType[];
  @Input() showAdvancedSearch: boolean;

  @Output() readonly search = new EventEmitter<TaskSearchCriteria>();
  @Output() readonly advancedSearch = new EventEmitter<boolean>();
  @Output() readonly submitClick = new EventEmitter<null>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  @ViewChild('summary') summary: ElementRef;

  taskStatusOptions = TASK_STATUS_OPTIONS;
  taskOutcomeOptions = TASK_OUTCOME_OPTIONS;

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
      deadlineFrom: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      deadlineTo: {
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
      taskStatus: 'OPEN',
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
      claimedOnFrom: [],
      claimedOnTo: [],
      createdOnFrom: [],
      createdOnTo: [],
      completedOnFrom: [],
      completedOnTo: [],
      deadlineFrom: [],
      deadlineTo: [],
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
