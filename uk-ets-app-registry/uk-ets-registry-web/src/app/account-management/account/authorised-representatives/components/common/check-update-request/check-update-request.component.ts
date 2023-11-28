import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { AuthorisedRepresentativesRoutePaths } from '@authorised-representatives/model/authorised-representatives-route-paths.model';
import {
  ARAccessRights,
  AuthorisedRepresentative,
} from '@shared/model/account';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-check-ar-update-request',
  templateUrl: './check-update-request.component.html',
  styleUrls: ['./check-update-request.component.scss'],
})
export class CheckUpdateRequestComponent extends UkFormComponent {
  @Input()
  updateType: AuthorisedRepresentativesUpdateType;
  @Input()
  existingAr: AuthorisedRepresentative;
  @Input()
  newAr: AuthorisedRepresentative;
  @Input()
  selectedArFromTable: AuthorisedRepresentative;
  @Input()
  newAccessRights: ARAccessRights;
  @Output()
  readonly clickChange = new EventEmitter<AuthorisedRepresentativesRoutePaths>();
  @Output() readonly submitRequest = new EventEmitter<string>();

  authorisedRepresentativesRoutePaths = AuthorisedRepresentativesRoutePaths;
  updateTypes = AuthorisedRepresentativesUpdateType;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  onChangeClicked(step: AuthorisedRepresentativesRoutePaths) {
    this.clickChange.emit(step);
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    this.submitRequest.emit(this.formGroup.value['comment']);
  }

  protected getFormModel(): any {
    return this.isForSuspensionOrRestoreOfAr()
      ? {
          comment: [
            '',
            { validators: Validators.required, updateOn: 'change' },
          ],
        }
      : {
          comment: ['', { updateOn: 'change' }],
        };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return this.isForSuspensionOrRestoreOfAr()
      ? {
          comment: {
            required: 'You must a enter a comment',
          },
        }
      : {
          comment: {},
        };
  }

  isForSuspensionOrRestoreOfAr() {
    return (
      this.updateType === AuthorisedRepresentativesUpdateType.SUSPEND ||
      this.updateType === AuthorisedRepresentativesUpdateType.RESTORE
    );
  }

  computeChangeUserLink() {
    switch (this.updateType) {
      case AuthorisedRepresentativesUpdateType.ADD:
        return AuthorisedRepresentativesRoutePaths.ADD_REPRESENTATIVE;
      case AuthorisedRepresentativesUpdateType.REPLACE:
        return AuthorisedRepresentativesRoutePaths.REPLACE_REPRESENTATIVE;
      case AuthorisedRepresentativesUpdateType.REMOVE:
      case AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS:
      case AuthorisedRepresentativesUpdateType.SUSPEND:
      case AuthorisedRepresentativesUpdateType.RESTORE:
        return AuthorisedRepresentativesRoutePaths.SELECT_AR_TABLE;
    }
  }
}
