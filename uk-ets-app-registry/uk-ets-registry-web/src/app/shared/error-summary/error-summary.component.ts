import {
  AfterViewChecked,
  Component,
  ElementRef,
  Inject,
  Input,
  ViewChild,
} from '@angular/core';
import { ErrorDetail, IErrorSummary } from '@shared/error-summary';
import { DOCUMENT } from '@angular/common';
import { PageScrollService } from 'ngx-page-scroll-core';
import { Store } from '@ngrx/store';
import { clearGoBackToErrorBasedPath } from '@shared/shared.action';
import { NavigationEnd, Router } from '@angular/router';
import { Location } from '@angular/common';
import { NavigationMessages, Paths } from '@shared/navigation-enums';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-error-summary',
  templateUrl: './error-summary.component.html',
})
export class ErrorSummaryComponent implements AfterViewChecked {
  _errorSummary: IErrorSummary;
  _goBackToErrorBasedPath: string;
  @ViewChild('errorsummary')
  errorSummaryElementRef: ElementRef;
  focusedElementId: string;
  routerEventsSubscription: Subscription;

  @Input()
  set errorSummary(errorSummary: IErrorSummary) {
    this._errorSummary = errorSummary;
  }

  @Input()
  set goBackToErrorBasedPath(backToErrorBasedPath: any) {
    this._goBackToErrorBasedPath = backToErrorBasedPath;
  }

  constructor(
    private pageScrollService: PageScrollService,
    private router: Router,
    private location: Location,
    private store: Store,
    @Inject(DOCUMENT) private document: Document
  ) {}

  ngOnInit() {
    this.subscribeToRouterEvents();
  }

  ngOnDestroy(): void {
    // Unsubscribe from router events to prevent memory leaks
    this.routerEventsSubscription.unsubscribe();
  }

  /**
   * this lifecycle hook guaranties that the focus will be performed after the @Input() changes
   * for the error summary make the div element with #errorsummary reference visible.
   */
  ngAfterViewChecked(): void {
    if (this._errorSummary.errors.length > 0) {
      this.errorSummaryElementRef.nativeElement.focus();
    }
  }

  /**
   * Scrolls to the form element with the error.
   * Setting interruptible to false so that the enter key has the same effect as the click
   * Also set duration to 100 for the scrolling to be faster.
   * @param elementId the ID of the html element
   */
  onClick(errorDetail: ErrorDetail): void {
    this.pageScrollService.scroll({
      document: this.document,
      duration: 100,
      interruptible: false,
      scrollTarget: '#' + errorDetail.componentId,
    });
    /**
     *
     * See https://pmo.trasys.be/jira/browse/UKETS-1398. Probably we'll need to move focus
     * to the erroneous component to complete this. For this one to work we need to get the correct
     * input id from the error detail. This is why am am leaving the unused injected Document here
     */
    setTimeout(() => {
      document.getElementById(errorDetail.componentId).focus();
    }, 100);

    // this one removes focus from the error summary after clicking the link in order to keep the implementation
    // exaclty the same as it is after https://pmo.trasys.be/jira/browse/UKETS-855
    setTimeout(() => {
      this.errorSummaryElementRef.nativeElement.blur();
    });
  }

  /**
   *
   * @param event
   * Changes the location without pushing into the router state history which resulted to browser back
   * button causing bugs. Navigates to /path given based on BR error.
   */
  navigateToAccountDetails(event): void {
    const path = this._goBackToErrorBasedPath;
    event.preventDefault();
    this.router
      .navigate([path], {
        skipLocationChange: true,
      })
      .then(() => this.location.replaceState(path));
    this.store.dispatch(clearGoBackToErrorBasedPath());
  }

  /**
   * @param path
   * Picks the navigation message that shows on the <a> tag based on the desired path.
   */
  getNavigationMessageBasedOnPath(path: string): string {
    switch (path) {
      case Paths.AccountList:
        return NavigationMessages.GoBackToTheAccountList;
      default:
        return NavigationMessages.GoBackDefault;
    }
  }

  /**
   * Subscribes to router change events in order to hide the go back
   * to error based path message in case the route changes without
   * the link being clicked.
   */
  private subscribeToRouterEvents(): void {
    this.routerEventsSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.store.dispatch(clearGoBackToErrorBasedPath());
      }
    });
  }
}
