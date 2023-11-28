import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { CheckUpdateRequestComponent } from './check-update-request.component';
import { TrustedAccountTableComponent } from '@shared/components/account/trusted-account-table';
import { APP_BASE_HREF } from '@angular/common';
import { RouterModule } from '@angular/router';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { provideMockStore } from '@ngrx/store/testing';
import { MockProtectPipe } from '../../../../../../testing/mock-protect.pipe';
import {
  ConcatDateTimePipe,
  GdsDateShortPipe,
  GdsTimePipe,
} from '@registry-web/shared/pipes';

describe('CheckUpdateRequestComponent', () => {
  let component: CheckUpdateRequestComponent;
  let fixture: ComponentFixture<CheckUpdateRequestComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterModule.forRoot([])],
        declarations: [
          CheckUpdateRequestComponent,
          TrustedAccountTableComponent,
          GovukTagComponent,
          MockProtectPipe,
          ConcatDateTimePipe,
          GdsTimePipe,
          GdsDateShortPipe,
        ],
        providers: [
          provideMockStore(),
          { provide: APP_BASE_HREF, useValue: '/' },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckUpdateRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
