import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnInit,
} from '@angular/core';
import { Observable } from 'rxjs';
import { KeycloakUser } from '@shared/user/keycloak-user';
import { Store } from '@ngrx/store';
import {
  selectARsInAccountDetails,
  selectEnrolmentKeyDetails,
  selectUserDetails,
  selectUserDetailsPendingTasks,
  selectUserFiles,
  selectUserHistoryAndComments,
} from '../../store/reducers';
import {
  ArInAccount,
  EnrolmentKey,
  ViewMode,
} from '@user-management/user-details/model';
import { enterRequestDocumentsWizard } from '@request-documents/wizard/actions';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import { FileDetails } from '@shared/model/file/file-details.model';
import { fetchUserFile } from '@user-management/user-details/store/actions';
import { ActivatedRoute, Router } from '@angular/router';
import { DomainEvent } from '@shared/model/event';
import { navigateToEmailChangeWizard } from '@email-change/action/email-change.actions';
import { actionNavigateToEnterReason } from '@user-management/token-change/action/token-change.actions';
import { navigateToPasswordChangeWizard } from '@user-management/password-change/action/password-change.actions';
import { UserDetailsUpdateWizardPathsModel } from '@user-update/model';
import * as UserDetailsActions from '@user-management/user-details/store/actions';
import {
  isAdmin,
  isSeniorAdmin,
  isSeniorOrJuniorAdmin,
  selectUrid,
} from '@registry-web/auth/auth.selector';
import {
  clearDeleteFileName,
  enterDeleteFileWizard,
} from '@registry-web/delete-file/wizard/actions/delete-file.actions';
import {
  selectFile,
  selectFileName,
} from '@registry-web/delete-file/wizard/reducers';
import { navigateToUserProfile } from '@shared/shared.action';
import { GoBackNavigationExtras } from '@shared/back-button';
import {
  selectGoBackToListNavigationExtras,
  selectGoBackToListRoute,
} from '@shared/shared.selector';

@Component({
  selector: 'app-user-details-container',
  templateUrl: './user-details-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserDetailsContainerComponent implements OnInit {
  @Input()
  backRoute: string;

  isSeniorOrJuniorAdmin$: Observable<boolean>;
  isSeniorAdmin$: Observable<boolean>;
  user$: Observable<KeycloakUser>;
  enrolmentKeyDetails$: Observable<EnrolmentKey>;
  userFiles$: Observable<FileDetails[]>;
  arInAccount$: Observable<ArInAccount[]>;
  userHistory$: Observable<DomainEvent[]>;
  hasUserDetailsPendingTask$: Observable<boolean>;
  fileDeleted$: Observable<string>;
  initiatorUrid$: Observable<string>;
  deletedFile: FileDetails;

  viewMode: ViewMode;
  isAdmin$: Observable<boolean>;
  goBackToListRoute$: Observable<string>;
  goBackToListNavigationExtras$: Observable<GoBackNavigationExtras>;

  constructor(
    private store: Store,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.user$ = this.store.select(selectUserDetails);
    this.enrolmentKeyDetails$ = this.store.select(selectEnrolmentKeyDetails);
    this.userFiles$ = this.store.select(selectUserFiles);
    this.arInAccount$ = this.store.select(selectARsInAccountDetails);
    this.userHistory$ = this.store.select(selectUserHistoryAndComments);
    this.hasUserDetailsPendingTask$ = this.store.select(
      selectUserDetailsPendingTasks
    );
    this.isSeniorOrJuniorAdmin$ = this.store.select(isSeniorOrJuniorAdmin);
    this.isSeniorAdmin$ = this.store.select(isSeniorAdmin);
    this.initiatorUrid$ = this.store.select(selectUrid);
    this.fileDeleted$ = this.store.select(selectFileName);
    // show banner for 3 seconds
    setTimeout(() => this.store.dispatch(clearDeleteFileName()), 3000);

    this.viewMode = this.route.snapshot.url
      .map((s) => s.path)
      .includes('my-profile')
      ? ViewMode.MY_PROFILE
      : ViewMode.USER_DETAILS;

    this.isAdmin$ = this.store.select(isAdmin);
    this.goBackToListRoute$ = this.store.select(selectGoBackToListRoute);

    this.goBackToListNavigationExtras$ = this.store.select(
      selectGoBackToListNavigationExtras
    );
  }

  getVisibilityBasedOnViewMode() {
    return ViewMode.MY_PROFILE !== this.viewMode;
  }

  onUserRequestDocuments({ recipientName, recipientUrid }) {
    this.store.dispatch(
      enterRequestDocumentsWizard({
        origin: RequestDocumentsOrigin.USER,
        originatingPath: this.router.url,
        documentsRequestType: DocumentsRequestType.USER,
        recipientName,
        recipientUrid,
      })
    );
  }

  ondDownloadFile(file: FileDetails) {
    this.store.dispatch(
      fetchUserFile({
        fileId: file.id,
      })
    );
  }

  onDeleteFile({ file, urid }) {
    this.store.dispatch(
      enterDeleteFileWizard({
        originatingPath: this.router.url,
        id: urid,
        file,
        documentsRequestType: DocumentsRequestType.USER,
      })
    );
  }

  startEmailChangeWizard(urid: string) {
    this.store.dispatch(
      navigateToEmailChangeWizard({
        caller: {
          route: this.router.url,
        },
        urid,
      })
    );
  }

  startTokenChangeWizard(urid: string) {
    this.store.dispatch(actionNavigateToEnterReason());
  }

  startPasswordChangeWizard(email: string) {
    this.store.dispatch(
      navigateToPasswordChangeWizard({
        email,
      })
    );
  }

  goToUpdateUserDetails(urid: string): void {
    this.store.dispatch(
      UserDetailsActions.navigateTo({
        route: `/user-details/${urid}/${UserDetailsUpdateWizardPathsModel.BASE_PATH}`,
        extras: {
          skipLocationChange: true,
          queryParams: {
            isMyProfilePage: true,
          },
        },
      })
    );
  }

  navigateToUserPage(urid: string) {
    const goBackRoute = this.route.snapshot['_routerState'].url;
    const userProfileRoute = '/user-details/' + urid;
    this.store.dispatch(
      navigateToUserProfile({ goBackRoute, userProfileRoute })
    );
  }
}
