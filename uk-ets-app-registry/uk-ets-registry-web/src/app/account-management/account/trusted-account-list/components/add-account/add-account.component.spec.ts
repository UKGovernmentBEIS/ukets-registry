import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddAccountComponent } from './add-account.component';
import {
  FormBuilder,
  FormGroupDirective,
  FormGroupName,
  ReactiveFormsModule,
} from '@angular/forms';
import { AccountInputComponent } from '@shared/form-controls/account-input/account-input.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
const formBuilder = new FormBuilder();

describe('AddAccountComponent', () => {
  let component: AddAccountComponent;
  let fixture: ComponentFixture<AddAccountComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [ReactiveFormsModule],
      declarations: [
        AddAccountComponent,
        AccountInputComponent,
        FormGroupDirective,
        FormGroupName,
      ],
      providers: [{ provide: FormBuilder, useValue: formBuilder }],
    }).compileComponents();
    fixture = TestBed.createComponent(AddAccountComponent);
    component = fixture.debugElement.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
