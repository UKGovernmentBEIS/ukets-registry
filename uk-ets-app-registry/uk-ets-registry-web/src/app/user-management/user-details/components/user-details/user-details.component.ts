import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { KeycloakUser } from '@shared/user/keycloak-user';
import * as SharedUtil from '@shared/shared.util';
import {
  ArInAccount,
  EnrolmentKey,
  UserDetailsSideMenu,
  VIEW_MODE_LABELS,
  ViewMode,
} from '@user-management/user-details/model';
import { FileDetails } from '@shared/model/file/file-details.model';
import { DomainEvent } from '@shared/model/event';
import { KeycloakUserDisplayNamePipe } from '@shared/pipes';
import { BannerType } from '@registry-web/shared/banner/banner-type.enum';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss'],
})
export class UserDetailsComponent implements OnInit {
  @Input() hasUserDetailsPendingTask: boolean;
  @Input() user: KeycloakUser;
  @Input() enrolmentKeyDetails: EnrolmentKey;
  @Input() documents: FileDetails[];
  @Input() arInAccount: ArInAccount[];
  @Input() domainEvents: DomainEvent[];
  @Input() currentViewMode: ViewMode;
  @Input() isSeniorOrJuniorAdmin: boolean;
  @Input() isSeniorAdmin: boolean;
  @Input() initiatorUrid: string;
  @Input() fileDeleted: string;
  @Input() isAdmin: boolean;

  canDeleteFile: boolean;

  @Output() readonly requestDocuments = new EventEmitter();
  @Output() readonly downloadFileEmitter = new EventEmitter<FileDetails>();
  @Output() readonly emailChange = new EventEmitter<string>();
  @Output() readonly tokenChange = new EventEmitter();
  @Output() readonly passwordChange = new EventEmitter();
  @Output() readonly userDetailsUpdateEmitter = new EventEmitter<string>();
  @Output() readonly deleteFileEmitter = new EventEmitter();
  @Output() readonly navigate = new EventEmitter<string>();

  sideMenuItems: string[];
  currentItem = 'User details';
  sharedUtil = SharedUtil;

  title: string;
  readonly viewMode = ViewMode;
  readonly BannerType = BannerType;

  constructor(private keycloakUserDisplayName: KeycloakUserDisplayNamePipe) {}

  ngOnInit() {
    this.sideMenuItems = UserDetailsSideMenu.UserDetailItems;
    this.title = VIEW_MODE_LABELS[this.currentViewMode].label;
    this.canDeleteFile =
      this.isSeniorOrJuniorAdmin &&
      !(this.initiatorUrid == this.user.attributes.urid[0]);
  }

  onRequestDocuments() {
    this.requestDocuments.emit({
      recipientName: this.keycloakUserDisplayName.transform(this.user),
      recipientUrid: this.user.attributes.urid[0],
    });
  }

  onDownloadFile($event: FileDetails) {
    this.downloadFileEmitter.emit($event);
  }

  onDeleteFile($event: FileDetails) {
    this.deleteFileEmitter.emit({
      file: $event,
      urid: this.user.attributes.urid[0],
    });
  }

  onEmailChange() {
    this.emailChange.emit(this.user.attributes.urid[0]);
  }

  onTokenChange() {
    this.tokenChange.emit(this.user.attributes.urid[0]);
  }

  onPasswordChange() {
    this.passwordChange.emit(this.user.email);
  }

  goToUpdateUserDetails(): void {
    this.userDetailsUpdateEmitter.emit(this.user.attributes.urid[0]);
  }

  navigateToUserPage(urid: string) {
    this.navigate.emit(urid);
  }
}
