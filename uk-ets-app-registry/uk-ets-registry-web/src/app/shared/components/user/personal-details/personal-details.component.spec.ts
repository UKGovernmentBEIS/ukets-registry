import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { PersonalDetailsComponent } from './personal-details.component';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { UkDateControlComponent } from '@registry-web/shared/form-controls';

const formBuilder = new FormBuilder();

describe('PersonalDetailsComponent', () => {
  let component: PersonalDetailsComponent;
  let fixture: ComponentFixture<PersonalDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          PersonalDetailsComponent,
          FormGroupDirective,
          ScreenReaderPageAnnounceDirective,
          ConnectFormDirective,
          UkDateControlComponent,
        ],
        imports: [ReactiveFormsModule],
        providers: [{ provide: FormBuilder, useValue: formBuilder }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonalDetailsComponent);
    component = fixture.componentInstance;
    component.countries = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
