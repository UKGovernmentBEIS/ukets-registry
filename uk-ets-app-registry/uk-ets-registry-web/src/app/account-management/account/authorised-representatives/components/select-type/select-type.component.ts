import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FormRadioGroupInfo,
  FormRadioOption,
} from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UkRegistryValidators } from '@shared/validation';
import {
  ArSubmittedUpdateRequest,
  AuthorisedRepresentative,
} from '@shared/model/account';
import { Configuration } from '@shared/configuration/configuration.interface';
import { getConfigurationValue } from '@shared/shared.util';

@Component({
  selector: 'app-select-ar-update-type',
  templateUrl: './select-type.component.html',
})
export class SelectTypeComponent extends UkFormComponent implements OnInit {
  @Output()
  readonly selectUpdateType =
    new EventEmitter<AuthorisedRepresentativesUpdateType>();
  @Input()
  updateType: AuthorisedRepresentativesUpdateType;
  @Input()
  updateTypes: FormRadioOption[];
  @Input()
  authorisedReps: AuthorisedRepresentative[];
  @Input()
  pendingARRequests: ArSubmittedUpdateRequest[];
  @Input()
  configuration: Configuration[];
  formRadioGroupInfo: FormRadioGroupInfo;
  activeARs: number;
  maxNumberOfARs: number;
  pendingARAddRequests: number;

  ngOnInit() {
    this.maxNumberOfARs = getConfigurationValue(
      'business.property.account.max.number.of.authorised.representatives',
      this.configuration
    );

    this.activeARs = this.authorisedReps.filter(
      (ar) => ar.state === 'ACTIVE' || ar.state === 'SUSPENDED'
    ).length;

    this.pendingARAddRequests = this.pendingARRequests.filter(
      (req) => req.updateType === AuthorisedRepresentativesUpdateType.ADD
    ).length;

    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Select type of update',
      radioGroupHeadingCaption:
        'Request to update the authorised representatives',
      radioGroupHint: 'Select one option',
      key: 'updateType',
      options: this.updateTypes,
    };
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    this.selectUpdateType.emit(this.formGroup.get('updateType').value);
  }

  protected getFormModel(): any {
    return {
      updateType: [
        this.updateType,
        Validators.compose([
          Validators.required,
          UkRegistryValidators.checkForMaxARsExceeded(
            this.activeARs,
            this.pendingARAddRequests,
            this.maxNumberOfARs
          ),
        ]),
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      updateType: {
        required: 'Select a type of update',
        maxARsExceeded:
          'The account has reached the maximum number of ' +
          this.maxNumberOfARs +
          ' Authorised Representatives' +
          (this.pendingARAddRequests > 0
            ? ', including pending approval tasks.'
            : '.'),
      },
    };
  }
}
