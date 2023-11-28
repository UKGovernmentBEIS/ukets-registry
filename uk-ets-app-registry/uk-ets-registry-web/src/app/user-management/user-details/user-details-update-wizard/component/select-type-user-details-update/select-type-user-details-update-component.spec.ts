import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { UserUpdateDetailsType } from '@user-update/model';
import { SelectTypeUserDetailsUpdateComponent } from './select-type-user-details-update-component';

const formBuilder = new FormBuilder();

describe('SelectTypeUserDetailsUpdateComponent', () => {
  let component: SelectTypeUserDetailsUpdateComponent;
  let fixture: ComponentFixture<SelectTypeUserDetailsUpdateComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [ReactiveFormsModule],
        declarations: [
          SelectTypeUserDetailsUpdateComponent,
          FormGroupDirective,
          ConnectFormDirective,
          ScreenReaderPageAnnounceDirective,
        ],
        providers: [{ provide: FormBuilder, useValue: formBuilder }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectTypeUserDetailsUpdateComponent);
    component = fixture.componentInstance;
    component.updateType = UserUpdateDetailsType.UPDATE_USER_DETAILS;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
