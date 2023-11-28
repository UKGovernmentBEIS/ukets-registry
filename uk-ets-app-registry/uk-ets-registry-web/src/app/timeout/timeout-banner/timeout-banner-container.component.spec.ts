import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TimeoutBannerContainerComponent } from './timeout-banner-container.component';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { selectSessionExpirationNotificationOffset } from '@shared/shared.selector';
import { TimeoutBannerComponent } from '@registry-web/timeout/timeout-banner/timeout-banner.component';
import { selectIsTimeoutDialogVisible } from '@registry-web/timeout/store/timeout.selectors';
import { SecondsToMinutesPipe } from '@registry-web/timeout/pipes/seconds-to-minutes.pipe';

describe('TimeoutBannerContainerComponent', () => {
  const originalConsole = console;

  let component: TimeoutBannerContainerComponent;
  let fixture: ComponentFixture<TimeoutBannerContainerComponent>;
  let store: MockStore;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          TimeoutBannerContainerComponent,
          TimeoutBannerComponent,
          SecondsToMinutesPipe,
        ],
        providers: [
          provideMockStore({
            selectors: [
              {
                selector: selectSessionExpirationNotificationOffset,
                value: 60,
              },
              {
                selector: selectIsTimeoutDialogVisible,
                value: false,
              },
            ],
          }),
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    console.error = jest.fn();
    console.warn = jest.fn();
    fixture = TestBed.createComponent(TimeoutBannerContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    store = TestBed.inject(MockStore);
  });

  afterEach(() => {
    console = originalConsole;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should logout now', () => {
    const storeSpy = jest.spyOn(store, 'dispatch');
    const buttons = fixture.nativeElement.querySelectorAll('button');
    buttons[0].click();
    fixture.detectChanges();
    expect(storeSpy).toHaveBeenCalledWith({
      shouldLogout: false,
      type: '[Shared] Hide timeout dialog',
    });
  });

  it('should extend session', () => {
    const storeSpy = jest.spyOn(store, 'dispatch');
    const buttons = fixture.nativeElement.querySelectorAll('button');
    buttons[1].click();
    fixture.detectChanges();
    expect(storeSpy).toHaveBeenCalledWith({
      shouldLogout: true,
      type: '[Shared] Hide timeout dialog',
    });
  });
});
