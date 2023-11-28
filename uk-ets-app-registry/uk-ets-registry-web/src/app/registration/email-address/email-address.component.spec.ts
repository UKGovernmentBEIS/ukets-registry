import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { EmailAddressComponent } from './email-address.component';
import { CookiesPopUpComponent } from '@shared/cookies-pop-up';
import { RouterTestingModule } from '@angular/router/testing';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';

const formBuilder = new FormBuilder();

@Component({
  selector: 'app-test-email-address',
  template: ` <app-email-address> </app-email-address> `,
})
class TestEmailAddressComponent {}

describe('EmailAddressComponent', () => {
  let component: TestEmailAddressComponent;
  let fixture: ComponentFixture<TestEmailAddressComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          FormGroupDirective,
          EmailAddressComponent,
          TestEmailAddressComponent,
          CookiesPopUpComponent,
          ScreenReaderPageAnnounceDirective,
        ],
        providers: [{ provide: FormBuilder, useValue: formBuilder }],
        imports: [RouterTestingModule, ReactiveFormsModule],
      }).compileComponents();
      fixture = TestBed.createComponent(TestEmailAddressComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    })
  );

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });

  test('Email is required ', async () => {
    const emailAddressComponent = fixture.debugElement.children[0]
      .componentInstance as EmailAddressComponent;

    const emailAddressControl = emailAddressComponent.formGroup.get('email');

    emailAddressControl.updateValueAndValidity();

    expect(emailAddressControl.hasError('required')).toBeTruthy();

    expect(emailAddressComponent.formGroup.status).toBe('INVALID');

    expect(emailAddressComponent.processValidationMessages().email).toBe(
      'Email address is required.'
    );
  });
});
