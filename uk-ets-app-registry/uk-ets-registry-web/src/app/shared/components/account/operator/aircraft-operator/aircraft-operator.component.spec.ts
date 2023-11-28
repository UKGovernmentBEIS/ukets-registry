import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SummaryListComponent } from '@shared/summary-list';
import { FormatUkDatePipe } from '@shared/pipes';
import { APP_BASE_HREF } from '@angular/common';
import {
  AircraftOperator,
  OperatorType,
  Regulator,
} from '@shared/model/account';
import { AircraftOperatorComponent } from './aircraft-operator.component';

describe('AircraftOperatorComponent', () => {
  let component: AircraftOperatorComponent;
  let fixture: ComponentFixture<AircraftOperatorComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          AircraftOperatorComponent,
          SummaryListComponent,
          FormatUkDatePipe,
        ],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          FormatUkDatePipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AircraftOperatorComponent);
    component = fixture.componentInstance;
    component.hasOperatorUpdatePendingApproval = false;
    component.canRequestUpdate = false;
    component.aircraft = {
      type: OperatorType.AIRCRAFT_OPERATOR,
      identifier: 1,
      regulator: Regulator.DAERA,
      changedRegulator: null,
      firstYear: '2021',
      lastYear: '2022',
      monitoringPlan: {
        id: '10000',
      },
    } as AircraftOperator;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
