import { Component, Inject, OnInit } from '@angular/core';
import { canGoBack } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { PageScrollService } from 'ngx-page-scroll-core';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-otp-authenticator',
  templateUrl: './otp-authenticator.component.html',
  styleUrls: [
    './otp-authenticator.component.scss',
    './guidance-content.min.scss',
  ],
})
export class OtpAuthenticatorComponent implements OnInit {
  enabledTopicForAndroid: boolean;
  enabledTopicDownloadingTheApp: boolean;
  enabledTopicForAppleIos: boolean;
  enabledTopicAccessingTheRegistry: boolean;
  enabledTopicSettingTheTimeOnDevice: boolean;

  constructor(
    private store: Store,
    private pageScrollService: PageScrollService,
    @Inject(DOCUMENT) private document: Document
  ) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/guidance/accessing-registry`,
      })
    );
    this.enabledTopicForAndroid = false;
    this.enabledTopicDownloadingTheApp = false;
    this.enabledTopicForAppleIos = false;
    this.enabledTopicAccessingTheRegistry = true;
    this.enabledTopicSettingTheTimeOnDevice = false;
  }

  clickTopicForAndroid() {
    this.enabledTopicForAndroid = true;
    this.enabledTopicDownloadingTheApp = false;
    this.enabledTopicForAppleIos = false;
    this.enabledTopicAccessingTheRegistry = false;
    this.enabledTopicSettingTheTimeOnDevice = false;
    this.scrollToTopHelpSystemDiv();
  }

  clickTopicDownloadingTheApp() {
    this.enabledTopicForAndroid = false;
    this.enabledTopicDownloadingTheApp = true;
    this.enabledTopicForAppleIos = false;
    this.enabledTopicAccessingTheRegistry = false;
    this.enabledTopicSettingTheTimeOnDevice = false;
    this.scrollToTopHelpSystemDiv();
  }

  clickTopicForAppleIos() {
    this.enabledTopicForAndroid = false;
    this.enabledTopicDownloadingTheApp = false;
    this.enabledTopicForAppleIos = true;
    this.enabledTopicAccessingTheRegistry = false;
    this.enabledTopicSettingTheTimeOnDevice = false;
    this.scrollToTopHelpSystemDiv();
  }

  clickTopicAccessingTheRegistry() {
    this.enabledTopicForAndroid = false;
    this.enabledTopicDownloadingTheApp = false;
    this.enabledTopicForAppleIos = false;
    this.enabledTopicAccessingTheRegistry = true;
    this.enabledTopicSettingTheTimeOnDevice = false;
    this.scrollToTopHelpSystemDiv();
  }

  clickTopicSettingTheTimeOnDevice() {
    this.enabledTopicForAndroid = false;
    this.enabledTopicDownloadingTheApp = false;
    this.enabledTopicForAppleIos = false;
    this.enabledTopicAccessingTheRegistry = false;
    this.enabledTopicSettingTheTimeOnDevice = true;
    this.scrollToTopHelpSystemDiv();
  }

  private scrollToTopHelpSystemDiv() {
    this.pageScrollService.scroll({
      document: this.document,
      duration: 250,
      interruptible: false,
      scrollTarget: this.document.documentElement,
    });
  }
}
