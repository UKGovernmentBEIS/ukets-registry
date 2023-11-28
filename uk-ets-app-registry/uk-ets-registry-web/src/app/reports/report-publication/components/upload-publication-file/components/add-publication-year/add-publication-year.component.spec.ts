import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPublicationYearComponent } from './add-publication-year.component';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { ConnectFormDirective } from '@shared/connect-form.directive';

describe('AddPublicationYearComponent', () => {
  let component: AddPublicationYearComponent;
  let fixture: ComponentFixture<AddPublicationYearComponent>;

  const formBuilder = new FormBuilder();
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        AddPublicationYearComponent,
        FormGroupDirective,
        ConnectFormDirective,
      ],
      imports: [ReactiveFormsModule],
      providers: [{ provide: FormBuilder, useValue: formBuilder }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddPublicationYearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
