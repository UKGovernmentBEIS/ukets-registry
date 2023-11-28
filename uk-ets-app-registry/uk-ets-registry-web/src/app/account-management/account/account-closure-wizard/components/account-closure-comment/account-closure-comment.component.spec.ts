import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  FormBuilder,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';
import { ConnectFormDirective } from '@shared/connect-form.directive';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { AccountClosureCommentComponent } from '@account-management/account/account-closure-wizard/components/account-closure-comment/account-closure-comment.component';

const formBuilder = new FormBuilder();

describe('AccountClosureCommentComponent', () => {
  let component: AccountClosureCommentComponent;
  let fixture: ComponentFixture<AccountClosureCommentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [ReactiveFormsModule],
      declarations: [
        AccountClosureCommentComponent,
        FormGroupDirective,
        ConnectFormDirective,
        ScreenReaderPageAnnounceDirective,
      ],
      providers: [{ provide: FormBuilder, useValue: formBuilder }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountClosureCommentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
