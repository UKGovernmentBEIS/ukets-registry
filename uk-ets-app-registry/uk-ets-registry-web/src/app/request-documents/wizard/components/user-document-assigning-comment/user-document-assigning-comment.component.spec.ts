import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { UkProtoFormCommentAreaComponent } from '@shared/form-controls/uk-proto-form-controls';
import { UserDocumentAssigningCommentComponent } from './user-document-assigning-comment.component';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';

const formBuilder = new FormBuilder();

describe('UserDocumentAssigningCommentComponent', () => {
  let component: UserDocumentAssigningCommentComponent;
  let fixture: ComponentFixture<UserDocumentAssigningCommentComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterTestingModule],
        declarations: [
          UserDocumentAssigningCommentComponent,
          UkProtoFormCommentAreaComponent,
        ],
        providers: [{ provide: FormBuilder, useValue: formBuilder }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDocumentAssigningCommentComponent);
    component = fixture.componentInstance;
    component.recipientName = 'Test name';
    component.comment = 'comment added';
    component.documentRequestType = DocumentsRequestType.USER;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the correct title', () => {
    expect(component.getTitleText()).toEqual('Request user documents');
    component.documentRequestType = DocumentsRequestType.ACCOUNT_HOLDER;
    expect(component.getTitleText()).toEqual(
      'Request account holder documents'
    );
  });
});
