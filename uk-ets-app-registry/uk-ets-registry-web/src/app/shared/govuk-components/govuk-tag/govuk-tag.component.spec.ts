import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { GovukTagComponent } from './govuk-tag.component';
import { Component } from '@angular/core';

describe('GovukTagComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    selector: 'app-test-component',
    template: ` <app-govuk-tag [color]="color"></app-govuk-tag> `,
  })
  class TestComponent {
    color = 'green';
  }

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [GovukTagComponent, TestComponent],
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

  it('should be in green color', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(
      element.querySelector<HTMLElement>('.govuk-tag').classList
    ).toContain('govuk-tag--green');
  });
});
