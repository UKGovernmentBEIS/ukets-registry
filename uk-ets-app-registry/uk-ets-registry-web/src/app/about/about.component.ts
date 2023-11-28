import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-about',
  imports: [RouterModule],
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <div class="govuk-grid-row">
          <div class="govuk-grid-column-two-thirds" aria-live="polite">
            <div class="govuk-breadcrumbs">
              <ol class="govuk-breadcrumbs__list">
                <li class="govuk-breadcrumbs__list-item">
                  <a class="govuk-breadcrumbs__link" routerLink="/dashboard"
                    >Home</a
                  >
                </li>
                <li class="govuk-breadcrumbs__list-item">About</li>
              </ol>
            </div>
          </div>
        </div>
        <div class="govuk-main-wrapper">
          <div>
            <span class="govuk-caption-xl"
              >Information about the application version</span
            >
            <h1 class="govuk-heading-xl">About</h1>
          </div>
          <p class="govuk-body">
            Version:
            <span class="govuk-!-font-weight-bold">RELEASE_VERSION</span>
          </p>
        </div>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AboutComponent {}
