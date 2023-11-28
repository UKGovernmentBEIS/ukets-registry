import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CheckAnwersAndSubmitComponent } from './check-anwers-and-submit.component';
import { FormGroupDirective } from '@angular/forms';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('CheckAnwersAndSubmitComponent', () => {
  let component: CheckAnwersAndSubmitComponent;
  let fixture: ComponentFixture<CheckAnwersAndSubmitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        CheckAnwersAndSubmitComponent,
        FormGroupDirective,
        ScreenReaderPageAnnounceDirective,
        ConnectFormDirective,
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckAnwersAndSubmitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
