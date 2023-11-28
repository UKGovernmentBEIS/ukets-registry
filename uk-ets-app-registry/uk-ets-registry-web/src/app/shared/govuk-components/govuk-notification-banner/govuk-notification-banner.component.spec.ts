import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  GovukNotificationBannerComponent,
  GovukNotificationBannerType,
} from '@shared/govuk-components';
import { Component } from '@angular/core';

describe('GovukNotificationBannerComponent', () => {
  @Component({
    selector: 'app-test-component',
    template: `
      <app-govuk-notification-banner
        title="Test title"
        [type]="type"
        role="region"
      >
        <p>Test content</p>
      </app-govuk-notification-banner>
    `,
  })
  class TestComponent {
    type: GovukNotificationBannerType = 'success';
  }

  function getBanner(f: ComponentFixture<TestComponent>): HTMLElement {
    return f.nativeElement.querySelector('.govuk-notification-banner');
  }

  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [GovukNotificationBannerComponent, TestComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain success class', () => {
    expect(getBanner(fixture).classList).toContain(
      'govuk-notification-banner--success'
    );
  });

  it('should contain warning class', () => {
    component.type = 'warning';
    fixture.detectChanges();
    expect(getBanner(fixture).classList).toContain(
      'govuk-notification-banner--warning'
    );
  });

  it('should project content', () => {
    const content = fixture.nativeElement.querySelector('p');
    expect(content.textContent).toEqual('Test content');
  });

  it('should contain title', () => {
    const title = fixture.nativeElement.querySelector('h2');
    expect(title.textContent.trim()).toEqual('Test title');
  });
});
