import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';

import { OperatorUpdateTaskDetailsComponent } from './operator-update-task-details.component';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { RequestType } from '@task-management/model';
import { OperatorSummaryChangesComponent } from '@shared/components/account/operator/operator-summary-changes';
import {
  Installation,
  InstallationActivityType,
  OperatorType,
  Regulator,
} from '@shared/model/account';
import { FormatUkDatePipe } from '@shared/pipes';
import { SummaryListComponent } from '@shared/summary-list';

describe('OperatorUpdateTaskDetailsComponent', () => {
  let component: OperatorUpdateTaskDetailsComponent;
  let fixture: ComponentFixture<OperatorUpdateTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          OperatorUpdateTaskDetailsComponent,
          OperatorSummaryChangesComponent,
          FormatUkDatePipe,
          SummaryListComponent,
        ],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          FormatUkDatePipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(OperatorUpdateTaskDetailsComponent);
    component = fixture.componentInstance;
    component.isInstallation = true;
    component.isAircraft = false;
    component.operatorUpdateTaskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST,
      accountInfo: null,
      current: {
        type: OperatorType.INSTALLATION,
        identifier: 1,
        regulator: Regulator.DAERA,
        changedRegulator: null,
        firstYear: '2021',
        lastYear: '2022',
        name: 'Name',
        activityType: InstallationActivityType.MANUFACTURE_OF_CERAMICS,
        permit: {
          id: '1',
          date: null,
        },
      } as Installation,
      changed: {
        type: OperatorType.INSTALLATION,
        identifier: 1,
        regulator: Regulator.DAERA,
        changedRegulator: Regulator.OPRED,
        name: 'Name',
        activityType: InstallationActivityType.COMBUSTION_OF_FUELS,
      } as Installation,
    };

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
