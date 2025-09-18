import {
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-skip-link',
  template: `
    <div #skipLink tabindex="-1">
      <a
        [routerLink]="[]"
        fragment="main-content"
        skipLocationChange
        data-module="govuk-skip-link"
        class="govuk-skip-link"
        >Skip to main content</a
      >
    </div>
  `,
})
export class SkipLinkComponent implements OnInit {
  @ViewChild('skipLink') skipLink: ElementRef;

  constructor(
    @Inject(DOCUMENT) private readonly document,
    private readonly router: Router
  ) {}

  ngOnInit() {
    this.router.events
      .pipe(filter((e) => e instanceof NavigationEnd))
      .subscribe((e: NavigationEnd) => {
        if (e.url.includes('#main-content')) {
          this.focusMainContent();
        } else {
          this.skipLink.nativeElement.focus();
        }
      });
  }

  focusMainContent(): void {
    const mainContainer: HTMLElement =
      this.document.querySelector('#main-content');
    mainContainer.focus();
  }
}
