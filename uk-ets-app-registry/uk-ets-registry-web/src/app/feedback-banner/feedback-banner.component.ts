import { Component } from '@angular/core';

@Component({
  selector: 'app-feedback-banner',
  template: `<div class="govuk-width-container">
    <div class="govuk-phase-banner">
      <p class="govuk-phase-banner__content">
        <span class="govuk-phase-banner__text">
          Your
          <a class="govuk-link" href="/digital-survey">feedback</a> is highly
          valued in helping to improve this service.
        </span>
      </p>
    </div>
    <br />
  </div>`,
})
export class FeedbackBannerComponent {}
