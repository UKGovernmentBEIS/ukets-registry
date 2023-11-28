import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TimeoutBannerComponent } from './timeout-banner.component';
import { SecondsToMinutesPipe } from '@registry-web/timeout/pipes/seconds-to-minutes.pipe';

describe('TimeoutBannerComponent', () => {
  const originalConsole = console;

  let component: TimeoutBannerComponent;
  let fixture: ComponentFixture<TimeoutBannerComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [TimeoutBannerComponent, SecondsToMinutesPipe],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    console.error = jest.fn();
    console.warn = jest.fn();
    fixture = TestBed.createComponent(TimeoutBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    console = originalConsole;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be opened', () => {
    component.isVisible = true;
    expect(component.isDialogOpen()).toBeTruthy();
  });

  it('should be closed', () => {
    component.isVisible = false;
    expect(component.isDialogOpen()).toBeFalsy();
  });

  it('should toggle dialog', () => {
    component.isVisible = true;
    expect(component.isDialogOpen()).toBeTruthy();

    component.isVisible = false;
    expect(component.isDialogOpen()).toBeFalsy();
  });
});
