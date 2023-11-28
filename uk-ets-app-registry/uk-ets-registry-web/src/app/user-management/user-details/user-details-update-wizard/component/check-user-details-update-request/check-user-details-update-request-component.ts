import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  UpdateUserDetailsRequestTypeMap,
  UserDetailsUpdateWizardPathsModel,
  UserUpdateDetailsType,
} from '@user-update/model';
import { IUser } from '@shared/user';
import { ErrorDetail } from '@shared/error-summary';
import { IUkOfficialCountry } from '@shared/countries/country.interface';

@Component({
  selector: 'app-check-user-details-update-request',
  templateUrl: `./check-user-details-update-request.html`,
})
export class CheckUserDetailsUpdateRequestComponent implements OnInit {
  @Input()
  userUpdateDetailsType: UserUpdateDetailsType;
  @Input()
  newUserDetails: IUser;
  @Input()
  currentUserDetails: IUser;
  @Input()
  isMyProfilePage: boolean;
  @Input() countries: IUkOfficialCountry[];
  @Output() readonly navigateToEmitter = new EventEmitter<string>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail>();
  @Output() readonly cancelEmitter = new EventEmitter();
  @Output() readonly submitRequest = new EventEmitter<IUser>();

  updateUserDetailsRequestTypeMap = UpdateUserDetailsRequestTypeMap;
  routePathForPersonalDetails: string;
  routePathForWorkDetails: string;
  routePathForMemorablePhrase: string;
  changedValues = {};

  ngOnInit() {
    if (
      this.userUpdateDetailsType === UserUpdateDetailsType.UPDATE_USER_DETAILS
    ) {
      this.routePathForPersonalDetails =
        UserDetailsUpdateWizardPathsModel.BASE_PATH +
        '/' +
        UserDetailsUpdateWizardPathsModel.PERSONAL_DETAILS;
      this.routePathForWorkDetails =
        UserDetailsUpdateWizardPathsModel.BASE_PATH +
        '/' +
        UserDetailsUpdateWizardPathsModel.WORK_DETAILS;
      this.routePathForMemorablePhrase =
        UserDetailsUpdateWizardPathsModel.BASE_PATH +
        '/' +
        UserDetailsUpdateWizardPathsModel.MEMORABLE_PHRASE;
    }
    this.changedValues = this.getObjectDiff(
      this.currentUserDetails,
      this.newUserDetails
    );
  }

  getObjectDiff(current, changed) {
    return {
      ...this.getOnlyChangedValues(current, changed),
    };
  }

  private getOnlyChangedValues(initialValues, updatedValues) {
    const diff = {};
    if (updatedValues) {
      Object.keys(updatedValues).forEach((r) => {
        if (updatedValues[r] !== initialValues[r]) {
          diff[r] = updatedValues[r];
        }
      });
    }
    return diff;
  }

  navigateTo(value) {
    this.navigateToEmitter.emit(value);
  }

  onSubmit() {
    if (!Object.keys(this.changedValues).length) {
      this.errorDetails.emit(
        new ErrorDetail(null, 'At least one field must be updated')
      );
    } else {
      this.submitRequest.emit(this.changedValues as IUser);
    }
  }

  onCancel() {
    this.cancelEmitter.emit();
  }
}
