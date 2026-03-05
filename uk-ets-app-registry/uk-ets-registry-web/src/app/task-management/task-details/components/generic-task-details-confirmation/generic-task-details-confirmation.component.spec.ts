import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskCompleteResponseBase } from '@shared/task-and-regulator-notice-management/model';
import { GenericTaskDetailsConfirmationComponent } from '@task-details/components';

describe('GenericTaskDetailsConfirmationComponent', () => {
  let component: GenericTaskDetailsConfirmationComponent;
  let fixture: ComponentFixture<GenericTaskDetailsConfirmationComponent>;

  const taskCompleteResponse: TaskCompleteResponseBase = {
    requestIdentifier: '1000001',
    taskDetailsDTO: null,
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GenericTaskDetailsConfirmationComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericTaskDetailsConfirmationComponent);
    component = fixture.componentInstance;
    component.taskCompleteResponse = taskCompleteResponse;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
