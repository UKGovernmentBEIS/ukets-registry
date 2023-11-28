import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

declare let gtag;

export interface gaEvent {
  eventCategory: string;
  eventAction: string;
  eventLabel?: string;
  eventValue?: number;
}

export interface Goal {
  name: string;
  time: number;
}

@Injectable({
  providedIn: 'root',
})
export class GaFactoryService {
  private gtagDisabled = false;
  private trackingId = '';

  private useGA(): boolean {
    return (
      this.trackingId && window['gtag'] !== undefined && !this.gtagDisabled
    );
  }

  /**
   *  Install global site tag (gtag.js) into the application
   */

  public initGA(trackingId) {
    this.trackingId = trackingId;

    const gTagScript = document.createElement('script');
    gTagScript.async = true;
    gTagScript.src = environment.gtag + this.trackingId;
    document.head.appendChild(gTagScript);

    if (window['gtag']) {
      return window['gtag'];
    }

    window['dataLayer'] = window['dataLayer'] || [];

    function gtag(...args) {
      // Disable the following rule based on ECMAScripts ES5 environment
      // https://eslint.org/docs/rules/prefer-rest-params#when-not-to-use-it
      // eslint-disable-next-line prefer-rest-params
      window['dataLayer'].push(arguments);
    }

    gtag('js', new Date());
    gtag('config', this.trackingId, {
      send_page_view: false,
    });

    return (window['gtag'] = gtag);
  }

  /**
   * Send manually a pageview hit to Google Analytics
   * @param url
   */

  public pageViewEmitter(url: string): void {
    if (!this.useGA()) return;
    gtag('config', this.trackingId, {
      page_path: url,
    });
  }

  public customPageView({ eventAction, eventPagePath }): void {
    if (!this.useGA()) return;
    gtag('event', eventAction, {
      page_path: eventPagePath,
    });
  }

  /**
   * Enable/ disable pageviews accordingly
   * @param sendViews
   */

  public sendPageviews(sendViews: boolean) {
    if (!this.useGA()) {
      return;
    }
    gtag('set', {
      send_page_view: sendViews,
    });
  }

  /**
   * Send Google Analytics Events
   */

  public eventEmitter({
    eventCategory,
    eventAction,
    eventLabel,
    eventValue,
  }: gaEvent): void {
    if (!this.useGA()) return;
    gtag('event', eventAction, {
      event_category: eventCategory,
      event_label: eventLabel,
      value: eventValue,
    });
  }

  /**
   * Send user timing information to Google Analytics
   * @param name
   * A string to identify the variable being recorded
   * @param time
   * The number of milliseconds in elapsed time
   * to report to Google Analytics
   * @param category
   * the name of user activity we want to track
   */
  public setDuration({ name, time, category }) {
    if (!this.useGA()) return;
    gtag('event', 'timing_complete', {
      name: name,
      value: time,
      event_category: category,
    });
  }

  public optOutMeasurement(status: boolean): void {
    let windowGA = window[`ga-disable-${this.trackingId}`];
    if (status) {
      windowGA = status;
    }
    if (windowGA) {
      windowGA = status;
    }
    this.gtagDisabled = status;
  }
}
