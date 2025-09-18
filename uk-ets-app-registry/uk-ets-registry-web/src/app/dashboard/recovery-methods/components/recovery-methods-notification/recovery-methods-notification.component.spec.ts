import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';
import { RecoveryMethodsNotificationComponent } from './recovery-methods-notification.component';
import { Store, StoreModule } from '@ngrx/store';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { navigateTo, retrieveUserRecoveryInfoSet } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { selectShowRecoveryNotificationPage } from '@registry-web/dashboard/recovery-methods/reducers';
import { hideRecoveryNotificationPage } from '@registry-web/dashboard/recovery-methods/actions/recovery-methods.actions';
const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));

describe('RecoveryMethodsNotificationComponent', () => {
  let component: RecoveryMethodsNotificationComponent;
  let fixture: ComponentFixture<RecoveryMethodsNotificationComponent>;
  let store: MockStore;
  let dispatchSpy: jasmine.Spy;

  const initialState = {
    shared: {
      recoveryInfoSet: false,
      hideRecoveryNotificationPage: false,
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RecoveryMethodsNotificationComponent],
      imports: [StoreModule.forRoot({})],
      providers: [
        provideMockStore({
          initialState: {
            shared: {
              recoveryInfoSet: false,
              hideRecoveryNotificationPage: false,
            },
          },
        }),
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    store = TestBed.inject(Store) as MockStore;
    dispatchSpy = spyOn(store, 'dispatch'); // Spy on dispatch calls
    store.overrideSelector(selectShowRecoveryNotificationPage, true);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RecoveryMethodsNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch retrieveUserRecoveryInfoSet action on init', () => {
    expect(dispatchSpy).toHaveBeenCalledWith(retrieveUserRecoveryInfoSet());
  });

  it('should show the notification page if showPage$ is true', () => {
    // Assuming showPage$ observable emits true
    store.overrideSelector(selectShowRecoveryNotificationPage, true);
    store.refreshState();
    fixture.detectChanges();

    const contentElement: DebugElement = fixture.debugElement.query(
      By.css('.govuk-grid-row')
    );
    expect(contentElement).toBeTruthy();
  });

  it('should dispatch navigateTo action with /dashboard when onSkip is called', () => {
    component.onSkip();
    expect(dispatchSpy).toHaveBeenCalledWith(
      navigateTo({ route: '/dashboard' })
    );
  });

  it("should dispatch hideRecoveryNotificationPage if 'Don't show this again' checkbox is checked and onSkip is called", () => {
    component.dontShowAgain = true;
    component.onSkip();
    expect(dispatchSpy).toHaveBeenCalledWith(hideRecoveryNotificationPage());
  });
});
