import { AfterViewInit, EventEmitter, Input, Output } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
} from '@angular/forms';
import { MessageSearchCriteria } from '@kp-administration/itl-messages/model';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { UkValidationMessageHandler } from '@registry-web/shared/validation';

@Component({
  selector: 'app-search-messages-form',
  templateUrl: './search-messages-form.component.html',
})
export class SearchMessagesFormComponent implements OnInit, AfterViewInit {
  form: UntypedFormGroup;
  @Input() storedCriteria: MessageSearchCriteria;
  @Output() readonly search = new EventEmitter<MessageSearchCriteria>();
  @Output() readonly submitClick = new EventEmitter<null>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  constructor(private formBuilder: UntypedFormBuilder) {}

  onClear() {
    this.form.reset();
  }

  ngOnInit() {
    this.initForm();
    this.validationMessages = {
      messageDateFrom: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      messageDateTo: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
    };

    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  private initForm() {
    this.form = this.formBuilder.group({
      messageId: [],
      messageDateFrom: [],
      messageDateTo: [],
    });
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

  ngAfterViewInit(): void {
    if (this.storedCriteria) {
      this.form.patchValue(this.storedCriteria);
    }
  }
}
