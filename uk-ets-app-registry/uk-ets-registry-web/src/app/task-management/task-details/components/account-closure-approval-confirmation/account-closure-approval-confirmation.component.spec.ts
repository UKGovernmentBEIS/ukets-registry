import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { AllocationTableApprovalConfirmationComponent } from '@task-details/components';
import { TaskCompleteResponseBase } from '@task-management/model/task-complete-response.model';

describe('AllocationTableApprovalConfirmationComponent', () => {
  let component: AllocationTableApprovalConfirmationComponent;
  let fixture: ComponentFixture<AllocationTableApprovalConfirmationComponent>;

  const taskCompleteResponse: TaskCompleteResponseBase = {
    requestIdentifier: '123456',
    taskDetailsDTO: null,
  };

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AllocationTableApprovalConfirmationComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(
      AllocationTableApprovalConfirmationComponent
    );
    component = fixture.componentInstance;
    component.taskCompleteResponse = taskCompleteResponse;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
