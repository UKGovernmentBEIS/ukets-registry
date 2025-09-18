import {
  AfterContentInit,
  Component,
  inject,
  Input,
  OnInit,
} from '@angular/core';
import { UkProtoFormCompositeComponent } from '@shared/form-controls/uk-proto-form-composite.component';
import {
  AbstractControl,
  ControlContainer,
  UntypedFormBuilder,
  UntypedFormGroup,
  FormGroupDirective,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import {
  ARSelectionType,
  AuthorisedRepresentative,
} from '@shared/model/account';
import { map, takeUntil } from 'rxjs';
import { ErrorDetail } from '@shared/error-summary';

@Component({
  selector: 'app-uk-select-authorised-representative',
  templateUrl: './uk-select-authorised-representative.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: UkSelectAuthorisedRepresentativeComponent,
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: UkSelectAuthorisedRepresentativeComponent,
      multi: true,
    },
  ],
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkSelectAuthorisedRepresentativeComponent
  extends UkProtoFormCompositeComponent
  implements AfterContentInit, Validator, OnInit
{
  arSelectionTypes = ARSelectionType;
  arSelectionType: ARSelectionType;

  @Input() authorisedRepresentatives: AuthorisedRepresentative[];
  @Input() requestedAuthorisedRepresentatives: AuthorisedRepresentative[];

  /**
   * Shows above radio buttons
   */
  @Input() hint = 'Select one option';

  @Input() hintClass = 'govuk-hint';

  @Input() showErrors: boolean;

  @Input() errorDetails: ErrorDetail[] = [];

  /**
   * If true the radio buttons and the dropdown are hidden.
   */
  @Input() showOnlyUserIdInput: false;

  private fb = inject(UntypedFormBuilder);

  ngOnInit(): void {
    super.ngOnInit();
    this.nestedForm
      .get('userIdInput')
      .valueChanges.pipe(
        map((val) => {
          if (val && val.indexOf(' ') >= 0) {
            // Remove whitespaces
            const trimmedVal = val.trim();
            this.nestedForm
              .get('userIdInput')
              .setValue(trimmedVal, { emitEvent: false });
          }
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  protected buildForm(): UntypedFormGroup {
    return this.fb.group({
      selectionTypeRadio: [''],
      listSelect: [''],
      userIdInput: [''],
    });
  }

  protected getDefaultErrorMessageMap(): { [p: string]: string } {
    return {
      typeSelectionRequired: 'Please specify the representative.',
      ArSelectionRequired: 'Please select the representative from the list.',
      alreadyExistsOnRequest:
        'The user is already added as an authorised representative on your request.',
      userIdRequired: 'Enter a User ID.',
    };
  }

  /**
   * Returns the default error messages in case of '{errorMessage: true}' so that a parent {@link UkFormComponent} does not need
   * to redefine them.
   */
  validate(control: AbstractControl): ValidationErrors {
    if (!control.value) {
      return {
        typeSelectionRequired:
          this.getDefaultErrorMessageMap().typeSelectionRequired,
      };
    }
    const selectionType = control.value.selectionTypeRadio;
    const listSelection = control.value.listSelect;
    const userId = control.value.userIdInput;
    if (selectionType === this.arSelectionTypes.FROM_LIST && !listSelection) {
      return {
        ArSelectionRequired:
          this.getDefaultErrorMessageMap().ArSelectionRequired,
      };
    }
    if (selectionType === this.arSelectionTypes.BY_ID && !userId) {
      return {
        userIdRequired: this.getDefaultErrorMessageMap().userIdRequired,
      };
    }

    const urid =
      selectionType === ARSelectionType.FROM_LIST ? listSelection : userId;
    if (
      this.requestedAuthorisedRepresentatives &&
      this.requestedAuthorisedRepresentatives.filter((ar) => ar.urid === urid)
        .length > 0
    ) {
      return {
        alreadyExistsOnRequest:
          this.getDefaultErrorMessageMap().alreadyExistsOnRequest,
      };
    }
  }

  /**
   * Resets the form elements when changing selection of type (radio button) to avoid sending both values in backend
   * (from select and from text field)
   */
  selectType(type: ARSelectionType) {
    this.arSelectionType = type;
    this.nestedForm.get('listSelect').reset();
    this.nestedForm.get('userIdInput').reset();
  }

  getErrors(): String[] {
    return this.errorDetails
      .filter((error) => error.componentId === 'userIdInput')
      .map((error) => error.errorMessage);
  }

  showError(): boolean {
    return this.errorDetails.length > 0 && this.showErrors;
  }
}

export class SelectArFormModel {
  selectionTypeRadio: ARSelectionType;
  listSelect: string;
  userIdInput: string;

  constructor({ selectionTypeRadio, listSelect, userIdInput }) {
    this.selectionTypeRadio = selectionTypeRadio;
    this.listSelect = listSelect;
    this.userIdInput = userIdInput;
  }

  /**
   * Retrieves custom value from composite component,
   * so as to avoid its FormGroup structure leaking into the components that use it.
   */
  getValue() {
    if (this.selectionTypeRadio === ARSelectionType.FROM_LIST) {
      return this.listSelect;
    } else {
      return this.userIdInput;
    }
  }
}
