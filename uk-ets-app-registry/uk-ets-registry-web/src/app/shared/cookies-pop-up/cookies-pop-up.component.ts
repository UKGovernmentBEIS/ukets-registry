import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-cookies-pop-up',
  templateUrl: './cookies-pop-up.component.html',
  styleUrls: ['./cookies-pop-up.component.scss'],
})
export class CookiesPopUpComponent {
  @Input()
  cookiesExpirationTime: string;
  @Input()
  areCookiesAccepted: boolean;
  @Input()
  areBrowserCookiesEnabled: boolean;

  @Output() readonly cookiesAcceptedEmitter = new EventEmitter<string>();

  show = false;

  cookiesNotAccepted() {
    return this.areCookiesAccepted === false;
  }

  acceptCookies() {
    this.show = true;
    this.cookiesAcceptedEmitter.emit(this.cookiesExpirationTime);
  }

  hideCookieMessage() {
    this.show = false;
  }

  goToSetPreferences() {
    location.href = '/cookies';
  }
}
