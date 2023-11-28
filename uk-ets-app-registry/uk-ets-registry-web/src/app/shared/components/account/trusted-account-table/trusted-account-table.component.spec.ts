import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TrustedAccountTableComponent } from './trusted-account-table.component';
import { GovukTagComponent } from '@shared/govuk-components/govuk-tag';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { MockProtectPipe } from '../../../../../testing/mock-protect.pipe';
import {
  ConcatDateTimePipe,
  GdsDateShortPipe,
  GdsTimePipe,
} from '@registry-web/shared/pipes';

describe('TrustedAccountTableComponent', () => {
  let component: TrustedAccountTableComponent;
  let fixture: ComponentFixture<TrustedAccountTableComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          TrustedAccountTableComponent,
          GovukTagComponent,
          MockProtectPipe,
          ConcatDateTimePipe,
          GdsTimePipe,
          GdsDateShortPipe,
        ],
        imports: [RouterTestingModule],
        providers: [provideMockStore()],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TrustedAccountTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
