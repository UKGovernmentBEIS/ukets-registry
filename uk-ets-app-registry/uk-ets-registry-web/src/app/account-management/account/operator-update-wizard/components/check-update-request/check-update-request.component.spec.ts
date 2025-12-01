import { CheckUpdateRequestComponent } from '@operator-update/components/check-update-request/check-update-request.component';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FormatUkDatePipe } from '@shared/pipes';
import { CancelRequestLinkComponent } from '@shared/components/account/cancel-request-link';
import { APP_BASE_HREF } from '@angular/common';
import { OperatorUpdateWizardPathsModel } from '@operator-update/model/operator-update-wizard-paths.model';
import {
  Installation,
  InstallationActivityType,
  OperatorType,
  Regulator,
} from '@shared/model/account';
import { ScreenReaderPageAnnounceDirective } from '@shared/directives/screen-reader-page-announce.directive';
import { OperatorSummaryChangesComponent } from '@shared/components/account/operator/operator-summary-changes';
import { SummaryListComponent } from '@shared/summary-list';

describe('CheckUpdateRequestComponent', () => {
  let component: CheckUpdateRequestComponent;
  let fixture: ComponentFixture<CheckUpdateRequestComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
      declarations: [
        CheckUpdateRequestComponent,
        CancelRequestLinkComponent,
        FormatUkDatePipe,
        ScreenReaderPageAnnounceDirective,
        OperatorSummaryChangesComponent,
        SummaryListComponent,
      ],
      providers: [{ provide: APP_BASE_HREF, useValue: '/' }, FormatUkDatePipe],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckUpdateRequestComponent);
    component = fixture.componentInstance;
    component.isInstallation = true;
    component.routePathForDetails = OperatorUpdateWizardPathsModel.BASE_PATH;
    component.currentOperatorInfo = {
      type: OperatorType.INSTALLATION,
      identifier: 10001,
      name: 'Installation name',
      activityTypes: [InstallationActivityType.CAPTURE_OF_GREENHOUSE_GASES],
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
      emitterId: '5653357886HT5',
    } as Installation;

    component.newOperatorInfo = {
      type: OperatorType.INSTALLATION,
      name: 'Installation name change',
      activityTypes: [InstallationActivityType.MANUFACTURE_OF_CERAMICS],
      permit: null,
      regulator: null,
      changedRegulator: Regulator.DAERA,
      firstYear: '2022',
      emitterId: '5653357886HT5',
    } as Installation;
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
  });
});
