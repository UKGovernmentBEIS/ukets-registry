import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { SearchTasksResultsComponent } from './search-tasks-results.component';
import { Task, TaskOutcome } from '@task-management/model';
import { By } from '@angular/platform-browser';
import { SortableColumnDirective } from 'src/app/shared/search/sort/sortable-column.directive';
import { SortableTableDirective } from 'src/app/shared/search/sort/sortable-table.directive';
import { SortService } from 'src/app/shared/search/sort/sort.service';
import { TaskSearchContainerComponent } from '../task-search-container.component';
import { BulkClaimComponent } from '../../bulk-claim/bulk-claim.component';
import { BulkAssignComponent } from '../../bulk-assign/bulk-assign.component';
import { ClaimAssignButtonGroupComponent } from '../claim-assign-button-group/claim-assign-button-group.component';
import { ToggleButtonComponent } from '@shared/search/toggle-button/toggle-button.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterLinkDirectiveStub } from 'src/app/shared/test/router-link-directive-stub';
import { BulkActionSuccessComponent } from '../../bulk-action-success/bulk-action-success.component';
import { NgbModule, NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { TypeAheadComponent } from '@shared/form-controls/type-ahead/type-ahead.component';
import { PaginatorComponent } from '@shared/search/paginator';
import { ProtectPipe } from '@shared/pipes/protect.pipe';
import { AuthApiService } from 'src/app/auth/auth-api.service';
import { MockAuthApiService } from 'src/testing/mock-auth-api-service';
import { StoreModule } from '@ngrx/store';
import * as fromAuth from 'src/app/auth/auth.reducer';
import { SearchTasksAdminCriteriaComponent } from '../search-tasks-form/search-tasks-admin-criteria/search-tasks-admin-criteria.component';
import { SearchTasksUserCriteriaComponent } from '../search-tasks-form/search-tasks-user-criteria/search-tasks-user-criteria.component';
import { AccountAccessPipe, GdsDateTimeShortPipe } from '@shared/pipes';
import { AccountAccessService } from '../../../../auth/account-access.service';
import { MockAccountAccessService } from '../../../../../testing/mock-account-access-service';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import {
  UkProtoFormCommentAreaComponent,
  UkProtoFormDatePickerComponent,
  UkProtoFormSelectComponent,
  UkProtoFormTextComponent,
} from '@shared/form-controls/uk-proto-form-controls';
import { DisableControlDirective } from '@shared/form-controls/disable-control.directive';
import {
  ReportSuccessBannerComponent,
  SearchReportButtonComponent,
} from '@shared/components/reports';
import { BannerComponent } from '@shared/banner/banner.component';
import { formatDate } from '@angular/common';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';

describe('SearchTasksResultsComponent', () => {
  let component: SearchTasksResultsComponent;
  let fixture: ComponentFixture<SearchTasksResultsComponent>;
  let router: Router;

  const DATE_CREATED = '2019-10-20T08:00:00+0000';

  const sortParameters: SortParameters = {
    sortField: 'CreatedOn',
    sortDirection: 'DESC',
  };

  const tasks: Task[] = [
    {
      requestId: 'test-requestId',
      taskType: 'test-taskType',
      initiatorName: 'test-initiatorName',
      claimantName: 'test-claimantName',
      completedByName: 'test-completer',
      accountNumber: 'test-accountNumber',
      accountFullIdentifier: 'test-account-full-identifier',
      accountType: 'test-accountType',
      kyotoAccountType: 'test-kyoto-accountType',
      registryAccountType: 'test-registry-accountType',
      accountHolder: 'test-accountHolder',
      authorisedRepresentative: 'test-authorisedRepresentative',
      authorizedRepresentativeUserId: 'test-authorisedRepresentative-id',
      transactionId: 'test-transactionId',
      createdOn: DATE_CREATED,
      taskStatus: 'CLAIMED',
      requestStatus: TaskOutcome.APPROVED,
      initiatedDate: '20/10/2019',
      claimedDate: '20/10/2019',
      completedDate: '21/10/2019',
      currentUserClaimant: true,
      recipientAccountNumber: 'recipient-account-number',
      accountStatus: 'OPEN',
      userHasAccess: true,
      accountTypeLabel: 'test-accountType',
    },
  ];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          FormsModule,
          NgbModule,
          NgbTypeaheadModule,
          RouterTestingModule.withRoutes([]),
          StoreModule.forRoot({ auth: fromAuth.reducer }),
        ],
        declarations: [
          TypeAheadComponent,
          SortableColumnDirective,
          SortableTableDirective,
          TaskSearchContainerComponent,
          SearchTasksAdminCriteriaComponent,
          SearchTasksUserCriteriaComponent,
          SearchTasksResultsComponent,
          PaginatorComponent,
          ClaimAssignButtonGroupComponent,
          BulkClaimComponent,
          BulkAssignComponent,
          ToggleButtonComponent,
          UkProtoFormDatePickerComponent,
          UkProtoFormSelectComponent,
          UkProtoFormTextComponent,
          UkProtoFormCommentAreaComponent,
          RouterLinkDirectiveStub,
          BulkActionSuccessComponent,
          ProtectPipe,
          GdsDateTimeShortPipe,
          AccountAccessPipe,
          GovukTagComponent,
          DisableControlDirective,
          ReportSuccessBannerComponent,
          SearchReportButtonComponent,
          BannerComponent,
        ],
        providers: [
          SortService,
          { provide: AuthApiService, useValue: MockAuthApiService },
          { provide: AccountAccessService, useClass: MockAccountAccessService },
        ],
      }).compileComponents();
      router = TestBed.inject(Router);
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchTasksResultsComponent);
    component = fixture.componentInstance;
    component.sortParameters = sortParameters;
    component.taskTypeOptionsAll = [
      { label: 'test-taskType', value: 'test-taskType' },
    ];
    component.accountTypeOptions = [
      { label: 'test-accountType', value: 'test-accountType' },
    ];
  });

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });

  test('the results are rendered in the corresponded columns', () => {
    component.results = tasks;
    component.selectedTasks = [];
    fixture.detectChanges();
    const columns = fixture.debugElement.queryAll(By.css('td'));
    const locale = 'en-US';
    const formattedDate = formatDate(DATE_CREATED, 'd LLL yyyy', locale);
    const formattedTime = formatDate(
      DATE_CREATED,
      'h:mma',
      locale
    ).toLowerCase();

    expect(tasks[0].requestId).toBe(
      columns[1].nativeElement.textContent.trim()
    );
    expect(tasks[0].taskType).toBe(columns[2].nativeElement.textContent.trim());
    expect(tasks[0].initiatorName).toBe(
      columns[3].nativeElement.textContent.trim()
    );
    expect(tasks[0].claimantName).toBe(
      columns[4].nativeElement.textContent.trim()
    );
    expect(tasks[0].accountFullIdentifier).toBe(
      columns[5].nativeElement.textContent.trim()
    );
    expect(tasks[0].accountType).toBe(
      columns[6].nativeElement.textContent.trim()
    );
    expect(tasks[0].accountHolder).toBe(
      columns[7].nativeElement.textContent.trim()
    );
    expect(columns[8].nativeElement.textContent.trim()).toBe(
      formattedDate + ', ' + formattedTime
    );
    expect(tasks[0].taskStatus.toLowerCase()).toBe(
      columns[9].nativeElement.textContent.trim().toLowerCase()
    );
    expect(tasks[0].requestStatus.toLowerCase()).toBe(
      columns[10].nativeElement.textContent.trim().toLowerCase()
    );
  });

  // TODO: change logic and re-enable this unit test.
  // routerlink is no longer user when navigating to taskdetails
  // it('can navigate and pass params to the task detail view', () => {
  //   component.results = tasks;
  //   component.selectedTasks = [];
  //   fixture.detectChanges(); // trigger initial data binding
  //
  //   // find DebugElements with an attached RouterLinkStubDirective
  //   const linkDes = fixture.debugElement.queryAll(
  //     By.directive(RouterLinkDirectiveStub)
  //   );
  //   // get attached link directive instances
  //   // using each DebugElement's injector
  //   const routerLinks = linkDes.map(de =>
  //     de.injector.get(RouterLinkDirectiveStub)
  //   );
  //
  //   const taskDetailsLinkDe = linkDes[0]; // First Task link DebugElement
  //   const taskDetailsLink = routerLinks[0]; // First Task link directive
  //
  //   expect(taskDetailsLink.navigatedTo).toBeNull();
  //
  //   taskDetailsLinkDe.triggerEventHandler('click', null);
  //   fixture.detectChanges();
  //
  //   expect(taskDetailsLink.navigatedTo).toEqual([
  //     '/task-details',
  //     'test-requestId'
  //   ]);
  // });
});
