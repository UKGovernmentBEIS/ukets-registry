import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { TaskDetailsComponent } from '@task-details/components';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RequestType, TaskOutcome } from '@task-management/model';
import { RouterTestingModule } from '@angular/router/testing';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { By } from '@angular/platform-browser';
import { GdsDateTimePipe } from '@registry-web/shared/pipes';

describe('TaskDetailsComponent', () => {
  let component: TaskDetailsComponent;
  let fixture: ComponentFixture<TaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, CommonModule, RouterTestingModule],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        providers: [GdsDateTimePipe],
        declarations: [TaskDetailsComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskDetailsComponent);
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

  it('should show warning for not completed upload document tasks, with uploaded files', () => {
    const taskDetails: RequestedDocumentUploadTaskDetails = {
      taskType: RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,
      accountHolderName: '',
      recipient: '',
      userUrid: '',
      documentNames: [],
      comment: '',
      referenceFiles: [],
      uploadedFiles: [{}],
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

    let warning = fixture.debugElement.query(
      By.css('.govuk-warning-text__assistive')
    );
    expect(warning).toBeDefined();

    taskDetails.taskStatus = 'COMPLETED';
    fixture.componentInstance.taskDetails = taskDetails;
    fixture.detectChanges();
    warning = fixture.debugElement.query(
      By.css('.govuk-warning-text__assistive')
    );
    expect(warning).toBeNull();

    (taskDetails as any).taskType = RequestType.ACCOUNT_TRANSFER;
    taskDetails.taskStatus = 'OPEN';
    fixture.componentInstance.taskDetails = taskDetails;
    fixture.detectChanges();
    warning = fixture.debugElement.query(
      By.css('.govuk-warning-text__assistive')
    );
    expect(warning).toBeNull();

    taskDetails.uploadedFiles = [];
    (taskDetails as any).taskType = RequestType.AH_REQUESTED_DOCUMENT_UPLOAD;
    fixture.componentInstance.taskDetails = taskDetails;
    fixture.detectChanges();
    warning = fixture.debugElement.query(
      By.css('.govuk-warning-text__assistive')
    );
    expect(warning).toBeNull();
  });
});
