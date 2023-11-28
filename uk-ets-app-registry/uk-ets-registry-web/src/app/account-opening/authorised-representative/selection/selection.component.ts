import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  ARSelectionType,
  AuthorisedRepresentative,
} from '@shared/model/account/authorised-representative';
import { AuthModel } from '../../../auth/auth.model';
import { AccountHolder } from '@shared/model/account/account-holder';
import { ErrorDetail } from '@shared/error-summary';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, ValidationErrors } from '@angular/forms';

@Component({
  selector: 'app-selection',
  templateUrl: './selection.component.html',
})
export class SelectionComponent extends UkFormComponent implements OnInit {
  @Input()
  accountHolder: AccountHolder;

  @Input()
  loggedInUser: AuthModel;

  @Input()
  fetchedAuthorisedRepresentatives: AuthorisedRepresentative[];

  @Input()
  authorisedRepresentatives: AuthorisedRepresentative[];

  @Output() readonly output = new EventEmitter<string>();

  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  validationErrorMessage: ValidationErrors = {};

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.showErrors = false;
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }

  getFormModel(): any {
    return {
      arSelection: [''],
    };
  }

  protected doSubmit() {
    this.showErrors = true;
    const arSelection = this.formGroup.get('arSelection').value;
    const urid =
      arSelection.selectionTypeRadio === ARSelectionType.FROM_LIST
        ? arSelection.listSelect
        : arSelection.userIdInput;

    if (
      arSelection.selectionTypeRadio === ARSelectionType.FROM_LIST &&
      (arSelection.listSelect === '' || arSelection.listSelect === 'null')
    ) {
      this.onError();
    } else if (
      arSelection.selectionTypeRadio === ARSelectionType.BY_ID &&
      arSelection.userIdInput === ''
    ) {
      this.onError();
    } else {
      this.output.emit(urid);
    }
  }

  onContinue() {
    super.onSubmit();
  }

  onError() {
    this.showErrors = true;
    this.validationErrorMessage = this.genericValidator.processMessages(
      this.formGroup
    );
    this.errorDetails.emit(
      this.genericValidator.mapMessagesToErrorDetails(
        this.validationErrorMessage
      )
    );
  }
}
