import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserDeactivationCommentComponent } from '@user-update/component/user-deactivation-comment/user-deactivation-comment.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { provideMockStore } from '@ngrx/store/testing';

const formBuilder = new FormBuilder();

describe('UserDeactivationCommentComponent', () => {
  let component: UserDeactivationCommentComponent;
  let fixture: ComponentFixture<UserDeactivationCommentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [ReactiveFormsModule],
      declarations: [
        UserDeactivationCommentComponent,
        FormGroupDirective,
        ConnectFormDirective,
        ScreenReaderPageAnnounceDirective,
      ],
      providers: [
        { provide: FormBuilder, useValue: formBuilder },
        provideMockStore(),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDeactivationCommentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
