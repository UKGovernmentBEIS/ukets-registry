import {
  waitForAsync,
  ComponentFixture,
  TestBed,
  tick,
  fakeAsync,
} from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ErrorSummaryComponent } from '@shared/error-summary/error-summary.component';
import { defaultPageScrollConfig, NGXPS_CONFIG } from 'ngx-page-scroll-core';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { clearGoBackToErrorBasedPath } from '@shared/shared.action';
import { NavigationMessages, Paths } from '@shared/navigation-enums';

function getLinkElements(fixture: ComponentFixture<ErrorSummaryComponent>) {
  return fixture.debugElement.queryAll(By.css('a'));
}

describe('ErrorSummaryComponent', () => {
  let store: MockStore;
  const initialState = {
    /* your state here */
  };
  const originalConsole = console;

  let component: ErrorSummaryComponent;
  let fixture: ComponentFixture<ErrorSummaryComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ErrorSummaryComponent],
        providers: [
          { provide: NGXPS_CONFIG, useValue: defaultPageScrollConfig },
          provideMockStore({ initialState }),
        ],
        imports: [RouterTestingModule],
      }).compileComponents();
      store = TestBed.inject(MockStore);
    })
  );

  beforeEach(() => {
    console.warn = jest.fn();
    fixture = TestBed.createComponent(ErrorSummaryComponent);
    component = fixture.componentInstance;

    component.errorSummary = { errors: [] }; // same as initial value from store

    fixture.detectChanges();
  });

  afterEach(() => {
    console = originalConsole;
  });

  it('should render no list elements initially', () => {
    const listElements = getLinkElements(fixture);

    expect(listElements.length).toEqual(0);
  });

  it('should render only links which have error messages', () => {
    component.errorSummary = {
      errors: [
        {
          componentId: 'firstName-label',
          errorMessage: '',
        },
        { componentId: 'lastName-label', errorMessage: 'some message' },
      ],
    };

    fixture.detectChanges();

    const listElements = getLinkElements(fixture);

    expect(listElements.length).toEqual(1);
  });

  it('should show navigation link when _goBackToErrorBasedPath is set', () => {
    component._goBackToErrorBasedPath = '/some-path';
    fixture.detectChanges();

    const linkElement = fixture.debugElement.query(
      By.css('.govuk-grid-columns-two-thirds a')
    );

    expect(linkElement).toBeTruthy();
  });

  it('should hide navigation link when _goBackToErrorBasedPath is not set', () => {
    component._goBackToErrorBasedPath = '';
    fixture.detectChanges();

    const linkElement = fixture.debugElement.query(
      By.css('.govuk-grid-columns-two-thirds a')
    );

    expect(linkElement).toBeNull();
  });

  it('should navigate to path and dispatch clearGoBackToErrorBasedPath action', fakeAsync(() => {
    const mockEvent = {
      preventDefault: jest.fn(),
    };
    const mockPath = '/some-path';
    component._goBackToErrorBasedPath = mockPath;

    const router = TestBed.inject(Router);
    const location = TestBed.inject(Location);
    const routerSpy = spyOn(router, 'navigate').and.returnValue(
      Promise.resolve(true)
    );
    const replaceStateSpy = spyOn(location, 'replaceState');
    const dispatchSpy = spyOn(store, 'dispatch').and.callThrough();

    component.navigateToAccountDetails(mockEvent);

    tick(); // simulates the passage of time until all pending asynchronous activities finish

    expect(mockEvent.preventDefault).toHaveBeenCalled();
    expect(routerSpy).toHaveBeenCalledWith([mockPath], {
      skipLocationChange: true,
    });
    expect(replaceStateSpy).toHaveBeenCalledWith(mockPath);
    expect(dispatchSpy).toHaveBeenCalledWith(clearGoBackToErrorBasedPath());
  }));

  it('should get navigation message based on path', () => {
    const mockPath = Paths.AccountList;

    const result = component.getNavigationMessageBasedOnPath(mockPath);

    expect(result).toBe(NavigationMessages.GoBackToTheAccountList);
  });

  it('should get default navigation message for unknown path', () => {
    const mockPath = '/unknown-path';

    const result = component.getNavigationMessageBasedOnPath(mockPath);

    expect(result).toBe(NavigationMessages.GoBackDefault);
  });
});
