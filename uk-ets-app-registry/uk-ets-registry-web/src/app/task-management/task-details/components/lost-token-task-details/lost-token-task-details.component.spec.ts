import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { LostTokenTaskDetailsComponent } from './lost-token-task-details.component';
import { SummaryListComponent } from '@shared/summary-list';
import { RequestType } from '@task-management/model';
import { RouterTestingModule } from '@angular/router/testing';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';

describe('LostTokenTaskDetailsComponent', () => {
  let component: LostTokenTaskDetailsComponent;
  let fixture: ComponentFixture<LostTokenTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [LostTokenTaskDetailsComponent, SummaryListComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(LostTokenTaskDetailsComponent);
    component = fixture.componentInstance;
    component.taskDetails = {
      taskType: RequestType.LOST_TOKEN,
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
