import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeTokenTaskDetailsComponent } from './change-token-task-details.component';
import { SummaryListComponent } from '@shared/summary-list';
import { RequestType } from '@task-management/model';
import { RouterTestingModule } from '@angular/router/testing';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';

describe('ChangeTokenTaskDetailsComponent', () => {
  let component: ChangeTokenTaskDetailsComponent;
  let fixture: ComponentFixture<ChangeTokenTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [ChangeTokenTaskDetailsComponent, SummaryListComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeTokenTaskDetailsComponent);
    component = fixture.componentInstance;
    component.taskDetails = {
      taskType: RequestType.CHANGE_TOKEN,
      comment: 'some comment',
      email: 'email@email.gr',
      firstName: 'First name',
      lastName: 'Last name',
      ...taskDetailsBase,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
