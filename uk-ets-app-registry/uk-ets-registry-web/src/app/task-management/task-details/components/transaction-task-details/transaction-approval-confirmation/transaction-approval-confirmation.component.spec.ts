import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionApprovalConfirmationComponent } from '@task-details/components';
import {
  ConcatDateTimePipe,
  GdsDateShortPipe,
  GdsTimePipe,
} from '@shared/pipes';
import { By } from '@angular/platform-browser';
import { LOCALE_ID } from '@angular/core';
import locale_en_GB from '@angular/common/locales/en-GB';
import { registerLocaleData } from '@angular/common';

describe('TransactionApprovalConfirmationComponent', () => {
  let component: TransactionApprovalConfirmationComponent;
  let fixture: ComponentFixture<TransactionApprovalConfirmationComponent>;

  registerLocaleData(locale_en_GB, 'en-GB');

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          TransactionApprovalConfirmationComponent,
          ConcatDateTimePipe,
          GdsTimePipe,
          GdsDateShortPipe,
        ],
        providers: [{ provide: LOCALE_ID, useValue: 'en-GB' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionApprovalConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render approval text in govuk-panel__title', () => {
    component.transactionProposalCompleteResponse = {
      requestIdentifier: '',
      taskDetailsDTO: null,
      transactionIdentifier: 'UK101231',
      executionTime: '12:11',
      executionDate: '20 Jan 2023',
    };

    fixture.detectChanges();
    const titleText = fixture.debugElement.query(By.css('.govuk-panel__title'));
    expect(titleText.nativeElement.innerHTML).toContain(
      'You have approved the<br>proposed transaction'
    );
  });

  test('should render transaction ID text in govuk-panel__body', () => {
    component.transactionProposalCompleteResponse = {
      requestIdentifier: '',
      taskDetailsDTO: null,
      transactionIdentifier: 'UK101231',
      executionTime: '12:11',
      executionDate: '20 Jan 2023',
    };

    fixture.detectChanges();
    const titleText = fixture.debugElement.queryAll(
      By.css('.govuk-panel__body')
    )[0];
    expect(titleText.nativeElement.textContent).toContain(
      'The transaction ID is UK101231'
    );
  });

  test.skip('should render transaction completion text in local date time', () => {
    component.transactionProposalCompleteResponse = {
      requestIdentifier: '',
      taskDetailsDTO: null,
      transactionIdentifier: 'UK101231',
      executionTime: '05:43pm',
      executionDate: '08 Feb 2023',
    };

    fixture.detectChanges();
    const titleText = fixture.debugElement.queryAll(
      By.css('.govuk-panel__body')
    )[1];
    expect(titleText.nativeElement.innerHTML).toContain(
      ' The estimated transaction completion<br>date/time is 8 Feb 2023, at 7:43pm. '
    );
  });
});
