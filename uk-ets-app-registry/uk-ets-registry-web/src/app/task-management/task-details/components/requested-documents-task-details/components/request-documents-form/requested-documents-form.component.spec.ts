import { RequestedDocumentsFormComponent } from '@task-details/components';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { UkSelectFileComponent } from '@shared/form-controls';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';
import {
  DocumentNamePipe,
  UploadedFilePipe,
} from '@registry-web/task-management/pipes';
import { taskDetailsBase } from '@registry-web/task-management/model/task-details.model.spec';
import { RequestType } from '@registry-web/task-management/model';

describe('RequestedDocumentsFormComponent', () => {
  let component: RequestedDocumentsFormComponent;
  let fixture: ComponentFixture<RequestedDocumentsFormComponent>;
  let store: MockStore<any>;
  let mockConfigurationSelector: any;
  const initialStateConfiguration = [
    {
      'registry.file.submit.document.type':
        'image/jpeg,application/pdf,image/png',
    },
    {
      'registry.file.max.size': '2048',
    },
  ];

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, CommonModule],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [
        RequestedDocumentsFormComponent,
        UkSelectFileComponent,
        UploadedFilePipe,
        DocumentNamePipe,
      ],
      providers: [provideMockStore()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestedDocumentsFormComponent);
    fixture.componentInstance.configuration = initialStateConfiguration;
    store = TestBed.inject(Store) as MockStore<any>;
    mockConfigurationSelector = store.overrideSelector(
      'configuration',
      initialStateConfiguration
    );
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({});
    component.taskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,
      claimantURID: 'UK100001',
      documentNames: [
        'Bank account details',
        'Proof of identity',
        'Proof of residence',
      ],
      accountHolderName: null,
      recipient: null,
      userUrid: null,
      accountHolderIdentifier: null,
      reasonForAssignment: null,
      comment: null,
      referenceFiles: [],
      uploadedFiles: [],
      completedDate: null,
      difference: null,
    };
    component.loggedinUser = {
      authenticated: true,
      showLoading: true,
      id: '100001',
      sessionUuid: 'b132b104-179e-45ba-a934-d090b6610312',
      urid: 'UK100001',
      username: 'test',
      roles: [],
      firstName: 'firstName',
      lastName: 'lastName',
      knownAs: 'KnownAs',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create documentNames.length - 1 form controls', () => {
    component.taskDetails.documentNames.forEach((s, index) => {
      expect(component.formGroup.controls['file-upload-' + index]).toBeTruthy();
    });
    expect(
      component.formGroup.controls[
        'file-upload-' + component.taskDetails.documentNames.length
      ]
    ).toBeFalsy();
  });
});
