import { OperatorUpdateComponent } from '@operator-update/components/operator-update/operator-update.component';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import {
  Installation,
  InstallationActivityType,
  MaritimeOperator,
  OperatorType,
  Regulator,
} from '@shared/model/account';
import { AircraftOperatorPipe, InstallationPipe, MaritimeOperatorPipe } from '@shared/pipes';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('OperatorUpdateComponent', () => {
  let component: OperatorUpdateComponent;
  let fixture: ComponentFixture<OperatorUpdateComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [
          OperatorUpdateComponent,
          InstallationPipe,
          AircraftOperatorPipe,
          MaritimeOperatorPipe
        ],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(OperatorUpdateComponent);
    component = fixture.componentInstance;
    component.operatorInfo = {
      type: OperatorType.INSTALLATION,
      identifier: 10001,
      name: 'Installation name',
      activityType: InstallationActivityType.CAPTURE_OF_GREENHOUSE_GASES,
      permit: {
        id: '12345',
        date: {
          day: '01',
          month: '02',
          year: '2021',
        },
      },
      regulator: Regulator.OPRED,
      changedRegulator: null,
      firstYear: '2021',
    } as Installation;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('MaritimeOperator functionality', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(OperatorUpdateComponent);
      component = fixture.componentInstance;
      component.operatorInfo = {
        type: OperatorType.MARITIME_OPERATOR,
        identifier: 100056,
        name: 'Test Maritime',
        monitoringPlan:{id:"56799653"},
        imo: "12345679H",
        regulator: Regulator.OPRED,
        firstYear: '2021',
      } as MaritimeOperator;
      fixture.detectChanges();
    });
  

    it('should render the Maritime Update section when a MaritimeOperator is selected', () => {
      const maritimeElement = fixture.debugElement.queryAll(By.css('app-maritime-update'));
      expect(maritimeElement.length).toEqual(1);
    });
  
    it('should NOT render the Aviation Update section when a MaritimeOperator is selected', () => {
      const aviatationElement = fixture.debugElement.queryAll(By.css('app-aircraft-update'));
      expect(aviatationElement.length).toEqual(0);
    });
  
    it('should NOT render the Installation Update section when a MaritimeOperator is selected', () => {
      const installationElement = fixture.debugElement.queryAll(By.css('app-installation-update'));
      expect(installationElement.length).toEqual(0);
    });
  });

});
