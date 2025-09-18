import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SummaryListComponent } from '@shared/summary-list';
import { FormatUkDatePipe } from '@shared/pipes';
import { APP_BASE_HREF } from '@angular/common';
import {
  MaritimeOperator,
  OperatorType,
  Regulator,
} from '@shared/model/account';
import { MaritimeOperatorComponent } from './maritime-operator.component';

describe('MaritimeOperatorComponent', () => {
  let component: MaritimeOperatorComponent;
  let fixture: ComponentFixture<MaritimeOperatorComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          MaritimeOperatorComponent,
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
    fixture = TestBed.createComponent(MaritimeOperatorComponent);
    component = fixture.componentInstance;
    component.hasOperatorUpdatePendingApproval = false;
    component.canRequestUpdate = false;
    component.maritime = {
      type: OperatorType.MARITIME_OPERATOR,
      identifier: 1,
      regulator: Regulator.DAERA,
      changedRegulator: null,
      firstYear: '2021',
      lastYear: '2022',
      monitoringPlan: {
        id: '10000',
      },
      emitterId: '5653357886HT5'
    } as MaritimeOperator;
    fixture.detectChanges();
  });

  it('should create a MaritimeOperator component', () => {
    expect(component).toBeTruthy();
  });
});
