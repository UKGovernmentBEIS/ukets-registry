import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  UserDetailsUpdateWizardPathsModel,
  UserUpdateDetailsType,
} from '@user-update/model';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { KeycloakUser } from '@shared/user';
import {
  ArInAccount,
  EnrolmentKey,
  ViewMode,
} from '@user-management/user-details/model';

@Component({
  selector: 'app-check-deactivation-details',
  templateUrl: './check-deactivation-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckDeactivationDetailsComponent implements OnInit {
  @Input()
  userUpdateDetailsType: UserUpdateDetailsType;
  @Input()
  countries: IUkOfficialCountry[];
  @Input()
  comment: string;
  @Input()
  userDetails: KeycloakUser;
  @Input()
  enrolmentKeyDetails: EnrolmentKey;
  @Input()
  isLoggedInUserSameAsDeactivated: boolean;
  @Input()
  arInAccounts: ArInAccount[];
  @Output()
  readonly navigateToEmitter = new EventEmitter<string>();
  @Output()
  readonly submitRequest = new EventEmitter<string>();

  readonly viewMode = ViewMode;

  routePathForDeactivationComment: string;

  ngOnInit(): void {
    this.routePathForDeactivationComment =
      UserDetailsUpdateWizardPathsModel.BASE_PATH +
      '/' +
      UserDetailsUpdateWizardPathsModel.DEACTIVATION_COMMENT;
  }

  onSubmit() {
    this.submitRequest.emit(this.comment);
  }

  navigateTo(value) {
    this.navigateToEmitter.emit(value);
  }
}
