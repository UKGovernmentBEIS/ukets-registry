import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { EmailChangeTaskDetailsComponent } from './email-change-task-details.component';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';

describe('EmailChangeTaskDetailsComponent', () => {
  let component: EmailChangeTaskDetailsComponent;
  let fixture: ComponentFixture<EmailChangeTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [EmailChangeTaskDetailsComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailChangeTaskDetailsComponent);
    component = fixture.componentInstance;
    component.taskDetails = {
      ...taskDetailsBase,
      initiatorCurrentEmail: '',
      initiatorFirstName: '',
      initiatorLastName: '',
      initiatorNewEmail: '',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
