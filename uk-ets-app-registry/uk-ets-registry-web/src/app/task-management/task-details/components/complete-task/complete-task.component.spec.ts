import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { MemoizedSelector } from '@ngrx/store';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { AuthState } from '@registry-web/auth/auth.reducer';
import { isAuthorityUser } from '@registry-web/auth/auth.selector';
import {
  UkProtoFormCommentAreaComponent,
  UkProtoFormTextComponent,
} from '@shared/form-controls/uk-proto-form-controls';
import { RequestType, TaskOutcome } from '@registry-web/task-management/model';
import { CompleteTaskComponent } from '.';
import { DisableControlDirective } from '@registry-web/shared/form-controls/disable-control.directive';

describe('CompleteTaskComponent', () => {
  let component: CompleteTaskComponent;
  let fixture: ComponentFixture<CompleteTaskComponent>;
  let store: MockStore;
  let mockIsAuthorityUserSelector: MemoizedSelector<AuthState, boolean>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        CompleteTaskComponent,
        UkProtoFormCommentAreaComponent,
        UkProtoFormTextComponent,
        DisableControlDirective,
      ],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [provideMockStore()],
    }).compileComponents();
    store = TestBed.inject(MockStore);
    mockIsAuthorityUserSelector = store.overrideSelector(
      isAuthorityUser,
      false
    );
    mockIsAuthorityUserSelector.setResult(false);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CompleteTaskComponent);
    component = fixture.componentInstance;
    component.taskDetails = {
      requestId: '1000192',
      initiatorName: 'Administrator UK ETS Realm',
      initiatorUrid: 'UK694094547713',
      claimantName: 'Registry Administrator Senior',
      claimantURID: 'UK802061511788',
      taskStatus: 'SUBMITTED_NOT_YET_APPROVED',
      requestStatus: null,
      initiatedDate: '2021-07-14 16:07:18.927',
      claimedDate: '2021-07-14 16:09:26.665',
      currentUserClaimant: false,
      completedByName: null,
      accountNumber: null,
      accountFullIdentifier: null,
      accountName: null,
      referredUserFirstName: null,
      referredUserLastName: null,
      referredUserURID: null,
      history: null,
      subTasks: null,
      parentTask: null,
      taskType: RequestType.EMISSIONS_TABLE_UPLOAD_REQUEST,
      fileName: null,
      fileSize: null,
    };
    component.taskOutcome = TaskOutcome.APPROVED;
    component.errorSummary = {
      errors: [
        {
          componentId: 'emissions-errors-file',
          errorMessage: 'The uploaded emissions excel contains errors.',
          errorId: 'EMISSIONS_FILE_BUSINESS_ERRORS',
          errorFileId: 76,
          errorFilename:
            'Error_UK_Emissions_28062021_SEPA_7C81DD27460435EAD91707259842DFB7.csv',
        },
      ],
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('submit', () => {
    it('should emit when the submit button is clicked', () => {
      const otpControl = fixture.componentInstance.form.get('otp');
      otpControl.setValue('123456');
      const commentControl = fixture.componentInstance.form.get('comment');
      commentControl.setValue('A comment to approve the task.');
      spyOn(component.completeTaskFormInfo, 'emit');
      component.submit();
      expect(component.completeTaskFormInfo.emit).toHaveBeenCalledWith({
        comment: 'A comment to approve the task.',
        otp: '123456',
      });
    });
  });

  describe('onDownloadErrorsCSV', () => {
    it('should emit when the download link is clicked', () => {
      spyOn(component.downloadErrorsCSV, 'emit');
      component.onDownloadErrorsCSV();
      expect(component.downloadErrorsCSV.emit).toHaveBeenCalledWith(76);
    });
  });

  describe('emissions errors file section visible', () => {
    it('should display download Panel when error file exists in summary', () => {
      const element = fixture.nativeElement.querySelector(
        '#emissions-errors-file'
      );
      expect(element).toBeTruthy();
    });
  });

  describe('emissions errors file section hidden', () => {
    it('should NOT display download Panel when error file exists in summary', () => {
      component.errorSummary = {
        errors: null,
      };
      fixture.detectChanges();
      const element = fixture.nativeElement.querySelector(
        '#emissions-errors-file'
      );
      expect(element).toBeFalsy();
    });
  });
});
