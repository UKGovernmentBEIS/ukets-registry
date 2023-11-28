import { OperatorUpdateComponent } from '@operator-update/components/operator-update/operator-update.component';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import {
  Installation,
  InstallationActivityType,
  OperatorType,
  Regulator,
} from '@shared/model/account';
import { AircraftOperatorPipe, InstallationPipe } from '@shared/pipes';
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
});
