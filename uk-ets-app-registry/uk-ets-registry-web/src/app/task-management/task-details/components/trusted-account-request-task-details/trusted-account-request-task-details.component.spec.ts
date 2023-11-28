import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TrustedAccountRequestTaskDetailsComponent } from './trusted-account-request-task-details.component';
import { AccountSummaryComponent } from '@shared/components/transactions/account-summary/account-summary.component';
import { CommonModule } from '@angular/common';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { TrustedAccountTableComponent } from '@shared/components/account/trusted-account-table';
import { TrustedAccountTaskDescriptionPipe } from '@task-management/pipes';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { provideMockStore } from '@ngrx/store/testing';
import { MockProtectPipe } from '../../../../../testing/mock-protect.pipe';
import {
  ConcatDateTimePipe,
  GdsDateShortPipe,
  GdsTimePipe,
} from '@registry-web/shared/pipes';

@Component({
  selector: 'app-dummy-component',
  template: '',
})
class DummyComponent {}

describe('TrustedAccountRequestTaskDetailsComponent', () => {
  let component: TrustedAccountRequestTaskDetailsComponent;
  let fixture: ComponentFixture<TrustedAccountRequestTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          CommonModule,
          RouterTestingModule.withRoutes([
            { path: 'task-details/history/', component: DummyComponent },
          ]),
        ],
        declarations: [
          TrustedAccountRequestTaskDetailsComponent,
          AccountSummaryComponent,
          TrustedAccountTableComponent,
          DummyComponent,
          TrustedAccountTaskDescriptionPipe,
          GovukTagComponent,
          MockProtectPipe,
          ConcatDateTimePipe,
          GdsTimePipe,
          GdsDateShortPipe,
        ],
        providers: [provideMockStore()],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(
      TrustedAccountRequestTaskDetailsComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
