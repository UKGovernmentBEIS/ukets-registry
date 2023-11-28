import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RequestedDocumentsTaskDetailsComponent } from '@task-details/components';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule, DatePipe, LowerCasePipe } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  RequestedDocumentUploadTaskDetails,
  RequestType,
  TaskOutcome,
} from '@task-management/model';
import { RouterTestingModule } from '@angular/router/testing';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { By } from '@angular/platform-browser';
import { GdsDateTimePipe } from '@registry-web/shared/pipes';

describe('RequestedDocumentsTaskDetailsComponent', () => {
  let component: RequestedDocumentsTaskDetailsComponent;
  let fixture: ComponentFixture<RequestedDocumentsTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, CommonModule, RouterTestingModule],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        providers: [GdsDateTimePipe],
        declarations: [RequestedDocumentsTaskDetailsComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestedDocumentsTaskDetailsComponent);
    component = fixture.componentInstance;
    component.configuration = [
      {
        'mail.etrAddress': 'blabla@gmail.com',
      },
    ];
    component.taskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,
      recipient: 'UK12345',
      // accountHolderIdentifier: 1,
      accountHolderName: 'test',
      userUrid: 'UK12345',
      documentNames: [],
      referenceFiles: [],
      uploadedFiles: [],
      //TODO add map if needed
      comment: null,
      completedDate: new Date(),
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show date of file upload', () => {
    const date = new Date();
    const taskDetails: RequestedDocumentUploadTaskDetails = {
      taskType: RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,
      accountHolderName: '',
      recipient: '',
      userUrid: '',
      documentNames: [],
      comment: '',
      referenceFiles: [],
      uploadedFiles: [
        { id: 0, fileName: 'testfile.txt', fileSize: '1mb' },
        { id: 1, fileName: 'testfile.txt', fileSize: '1mb' },
      ],
      requestId: '',
      initiatorName: '',
      initiatorUrid: '',
      claimantName: '',
      claimantURID: '',
      taskStatus: '',
      requestStatus: TaskOutcome.APPROVED,
      initiatedDate: '',
      claimedDate: '',
      currentUserClaimant: false,
      completedByName: '',
      accountNumber: '',
      accountFullIdentifier: '',
      accountName: '',
      referredUserFirstName: '',
      referredUserLastName: '',
      referredUserURID: '',
      history: [],
      subTasks: [],
      parentTask: undefined,
      reasonForAssignment: '',
      completedDate: date,
    };
    fixture.componentInstance.taskDetails = taskDetails;
    const pipe = new GdsDateTimePipe('en');
    const uploadDateElements = fixture.debugElement.queryAll(
      By.css('.file-date')
    );

    uploadDateElements.forEach((uploadDateElement) => {
      expect(uploadDateElement.nativeElement.textContent).toContain(
        pipe.transform(uploadDateElement.nativeElement.textContent)
      );
    });
  });

  it('should show Complete task button and hint for Request Document tasks', () => {
    const taskDetails: RequestedDocumentUploadTaskDetails = {
      taskType: RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,
      accountHolderName: '',
      recipient: '',
      userUrid: '',
      documentNames: [],
      comment: '',
      referenceFiles: [],
      uploadedFiles: [],
      requestId: '',
      initiatorName: '',
      initiatorUrid: '',
      claimantName: '',
      claimantURID: '',
      taskStatus: 'OPEN',
      requestStatus: TaskOutcome.SUBMITTED_NOT_YET_APPROVED,
      initiatedDate: '',
      claimedDate: '',
      currentUserClaimant: true,
      completedByName: '',
      accountNumber: '',
      accountFullIdentifier: '',
      accountName: '',
      referredUserFirstName: '',
      referredUserLastName: '',
      referredUserURID: '',
      history: [],
      subTasks: [],
      parentTask: undefined,
      reasonForAssignment: '',
      completedDate: new Date(),
    };

    fixture.componentInstance.taskDetails = taskDetails;
    fixture.componentInstance.taskActionsVisibility = true;

    let button = fixture.debugElement.query(By.css('.govuk-button'));
    let hint = fixture.debugElement.query(By.css('.hint'));

    const checkIfCanOnlyCompleteSpy = spyOn(
      component,
      'checkIfCanOnlyComplete'
    );
    checkIfCanOnlyCompleteSpy.and.returnValue(true);

    expect(button).toBeDefined();
    expect(hint).toBeDefined();

    taskDetails.taskStatus = 'COMPLETED';
    fixture.componentInstance.taskDetails = taskDetails;
    fixture.detectChanges();

    button = fixture.debugElement.query(By.css('.govuk-button'));
    hint = fixture.debugElement.query(By.css('.hint'));
    expect(button).toBeNull();
    expect(hint).toBeNull();

    taskDetails.taskStatus = 'OPEN';
    fixture.componentInstance.taskDetails = taskDetails;
    checkIfCanOnlyCompleteSpy.and.returnValue(false);
    fixture.detectChanges();

    button = fixture.debugElement.query(By.css('.govuk-button'));
    hint = fixture.debugElement.query(By.css('.hint'));
    expect(button).toBeNull();
    expect(hint).toBeNull();

    (taskDetails as any).taskType = RequestType.ACCOUNT_TRANSFER;
    fixture.componentInstance.taskDetails = taskDetails;
    checkIfCanOnlyCompleteSpy.and.returnValue(true);
    fixture.detectChanges();

    button = fixture.debugElement.query(By.css('.govuk-button'));
    hint = fixture.debugElement.query(By.css('.hint'));
    expect(button).toBeNull();
    expect(hint).toBeNull();
  });
});
