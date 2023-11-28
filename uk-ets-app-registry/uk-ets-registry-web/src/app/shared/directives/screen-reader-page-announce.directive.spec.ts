import { Component } from '@angular/core';
import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';

@Component({
  selector: 'app-test-comp',
  template: `
    <div appScreenReaderPageAnnounce [pageTitle]="'Test title'"></div>
  `,
})
class TestComponent {}

describe('ScreenReaderPageAnnounceDirective', () => {
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [ScreenReaderPageAnnounceDirective, TestComponent],
    }).createComponent(TestComponent);

    fixture.detectChanges();
  });

  it('should return the expected page title', fakeAsync(() => {
    tick(500);
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      expect(
        document.getElementsByClassName('govuk-visually-hidden')[0]['innerText']
      ).toEqual('Test title');
    });
  }));
});
