import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CookieService {
  PREFERENCES_SET_COOKIE = 'uk_ets_cookies_preferences_set';
  COOKIES_POLICY = 'uk_ets_cookies_policy';

  /**
   * Accept all cookies in the application.
   */
  acceptAllCookies(cookiesExpirationTime: number) {
    const d = new Date();
    d.setTime(
      d.getTime() + Number(cookiesExpirationTime) * 24 * 60 * 60 * 1000
    ); // Valid for 1 year
    this.setCookie(this.PREFERENCES_SET_COOKIE, 'true', {
      // secure: true,
      expires: d
    });
    this.setCookie(
      this.COOKIES_POLICY,
      JSON.stringify({
        essential: true,
        usage: true
      }),
      {
        // secure: true,
        expires: d
      }
    );
    return true;
  }

  /**
   * Check if the Cookies have been accepted by the user
   */
  notAccepted() {
    return this.getCookie(this.PREFERENCES_SET_COOKIE) === null;
  }

  /**
   * Check if the Browser has the Cookies enabled.
   */
  checkIfCookiesEnabled() {
    return navigator.cookieEnabled;
  }

  /**
   * Set a new Cookie
   * @param name The name of the Cookie
   * @param value The value of the Cookie
   * @param options Additional configuration of the Cookie
   */
  private setCookie(name, value, options) {
    const cookieOptions = {
      path: '/',
      ...options
    };

    if (cookieOptions.expires instanceof Date) {
      cookieOptions.expires = cookieOptions.expires.toUTCString();
    }

    let updatedCookie = name + '=' + value;

    Object.keys(cookieOptions).forEach(option => {
      updatedCookie += '; ' + option;
      const optionValue = cookieOptions[option];
      if (optionValue !== true) {
        updatedCookie += '=' + optionValue;
      }
    });

    document.cookie = updatedCookie;
  }

  /**
   * Get the value of a specific Cookie if exists
   * @param name The name of the Cookie
   */
  getCookie(name) {
    const cookieName = name + '=';
    const cookieValue = document.cookie.split(';');
    for (const cookie of cookieValue) {
      let c = cookie;
      while (c.charAt(0) === ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(cookieName) === 0) {
        return c.substring(cookieName.length, c.length);
      }
    }
    return null;
  }
}
