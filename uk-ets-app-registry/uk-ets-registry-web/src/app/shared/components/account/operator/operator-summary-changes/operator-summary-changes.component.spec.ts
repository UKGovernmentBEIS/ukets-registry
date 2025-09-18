import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FormatUkDatePipe } from '@shared/pipes';
import { OperatorSummaryChangesComponent } from './operator-summary-changes.component';
import { APP_BASE_HREF } from '@angular/common';
import {
  Installation,
  InstallationActivityType,
  MaritimeOperator,
  OperatorType,
  Regulator,
} from '@shared/model/account';
import { SummaryListComponent } from '@shared/summary-list';

describe('OperatorSummaryChangesComponent', () => {
  let component: OperatorSummaryChangesComponent;
  let fixture: ComponentFixture<OperatorSummaryChangesComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          FormatUkDatePipe,
          OperatorSummaryChangesComponent,
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
    fixture = TestBed.createComponent(OperatorSummaryChangesComponent);
    component = fixture.componentInstance;
    component.routePathForDetails = null;
    component.isWizardOrientedFlag = false;
    component.isInstallation = true;
    component.isAircraft = false;
    component.isMaritime = false;
    component.current = {
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
      emitterId: '5653357886HT5'
    } as Installation;
    component.changed = {
      type: OperatorType.INSTALLATION,
      identifier: 1,
      regulator: Regulator.DAERA,
      changedRegulator: Regulator.OPRED,
      name: 'Name',
      activityType: InstallationActivityType.COMBUSTION_OF_FUELS,
    } as Installation;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('MaritimeOperator functionality', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(OperatorSummaryChangesComponent);
      component = fixture.componentInstance;
      component.routePathForDetails = null;
      component.isWizardOrientedFlag = false;
      component.isInstallation = false;
      component.isAircraft = false;
      component.isMaritime = true;

      component.current = {
        type: OperatorType.MARITIME_OPERATOR,
        identifier: 1000009,
        monitoringPlan:{id:"56799653"},
        imo: "12345679H",
        emitterId: '5653357886HT5'
      } as MaritimeOperator;

      component.changed = {
        type: OperatorType.MARITIME_OPERATOR,
        identifier: 1000099,
        monitoringPlan:{id:"56799333"},
        imo: "12349979H",
        emitterId: '5653357886ZZ5'
      } as MaritimeOperator;

      fixture.detectChanges();
    });

    it('should prefix the title with "Maritime" when the Operator type is Maritime', () => {
      expect(component.title).toBe('Maritime operator details');
    });

    it('should prefix the titleOfIdentifier with "Maritime" when the Operator type is Maritime', () => {
      expect(component.titleOfIdentifier).toBe('Maritime Operator ID');
    });
  });

});
