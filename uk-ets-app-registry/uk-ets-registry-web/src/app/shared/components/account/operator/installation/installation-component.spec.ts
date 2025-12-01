import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FormatUkDatePipe } from '@shared/pipes';
import { SummaryListComponent } from '@shared/summary-list';
import { APP_BASE_HREF } from '@angular/common';
import { InstallationComponent } from './installation.component';
import {
  Installation,
  InstallationActivityType,
  OperatorType,
  Regulator,
} from '@shared/model/account';

describe('InstallationComponent', () => {
  let component: InstallationComponent;
  let fixture: ComponentFixture<InstallationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
      declarations: [
        InstallationComponent,
        SummaryListComponent,
        FormatUkDatePipe,
      ],
      providers: [{ provide: APP_BASE_HREF, useValue: '/' }, FormatUkDatePipe],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstallationComponent);
    component = fixture.componentInstance;
    component.hasOperatorUpdatePendingApproval = false;
    component.canRequestUpdate = false;
    component.installation = {
      type: OperatorType.INSTALLATION,
      identifier: 1,
      regulator: Regulator.DAERA,
      changedRegulator: null,
      firstYear: '2021',
      lastYear: '2022',
      name: 'Name',
      activityTypes: [InstallationActivityType.MANUFACTURE_OF_CERAMICS],
      permit: {
        id: '1',
        date: null,
      },
      emitterId: '35675656767HT',
    } as Installation;
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
  });
});
