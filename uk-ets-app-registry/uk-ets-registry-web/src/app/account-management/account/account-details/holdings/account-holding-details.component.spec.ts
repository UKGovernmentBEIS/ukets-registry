import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AccountHoldingDetailsComponent } from '@account-management/account/account-details/holdings/account-holding-details.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { AccountHoldingDetails } from '@account-management/account/account-details/holdings/account-holding-details.model';
import { provideMockStore } from '@ngrx/store/testing';
import { By } from '@angular/platform-browser';
import {
  EnvironmentalActivity,
  EnvironmentalActivityLabelMap,
} from '@shared/model/transaction';
import { MockProtectPipe } from 'src/testing/mock-protect.pipe';

describe('AccountHoldingDetailsComponent', () => {
  let component: AccountHoldingDetailsComponent;
  let fixture: ComponentFixture<AccountHoldingDetailsComponent>;
  let accountHoldingDetails: AccountHoldingDetails;

  function initAccountHoldingDetails() {
    accountHoldingDetails = {
      errorSummary: null,
      results: [
        {
          reserved: true,
          quantity: 30,
          serialNumberEnd: 1070,
          serialNumberStart: 1040,
          project: 'a project',
          activity: 'DEFORESTATION',
        },
      ],
      unit: 'AAU',
      applicablePeriod: 'CP1',
      originalPeriod: 'CP1',
    };
  }

  beforeEach(
    waitForAsync(() => {
      initAccountHoldingDetails();
      TestBed.configureTestingModule({
        declarations: [AccountHoldingDetailsComponent, MockProtectPipe],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              data: of({
                details: {
                  details: accountHoldingDetails,
                  accountType: 'PARTY_HOLDING_ACCOUNT',
                },
              }),
            },
          },
          provideMockStore(),
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountHoldingDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render Summary list in light grey color', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.ukets-background-light-grey')
    )[0];
    expect(key).toBeTruthy();
  });

  test('the unit type label is rendered correctly', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[0];
    expect(key.nativeElement.textContent.trim()).toEqual('Unit:');
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[0];
    expect(value.nativeElement.textContent.trim()).toEqual('AAU');
  });

  test('the original CP is rendered correctly', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[1];
    expect(key.nativeElement.textContent.trim()).toEqual('Original CP:');
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[1];
    expect(value.nativeElement.textContent.trim()).toEqual('1');
  });

  test('the Applicable CP is rendered correctly', () => {
    const key = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__key')
    )[2];
    expect(key.nativeElement.textContent.trim()).toEqual('Applicable CP:');
    const value = fixture.debugElement.queryAll(
      By.css('.govuk-summary-list__value')
    )[2];
    expect(value.nativeElement.textContent.trim()).toEqual('1');
  });

  test('The headers of the result table are rendered correctly', () => {
    const expectedValues = [
      'Serial number start',
      'Serial number end',
      'Project',
      'Activity',
      'Quantity',
      'Reserved',
    ];
    const headers = fixture.debugElement.queryAll(By.css('th'));
    headers.forEach((header, index) => {
      expect(headers[index].nativeElement.textContent.trim()).toEqual(
        expectedValues[index]
      );
    });
  });

  test('The unit blocks are rendered correctly', () => {
    const unitBlock = accountHoldingDetails.results[0];
    const expectedValues = [
      `${unitBlock.serialNumberStart}`,
      `${unitBlock.serialNumberEnd}`,
      unitBlock.project,
      EnvironmentalActivityLabelMap.get(
        EnvironmentalActivity[unitBlock.activity]
      ),
      `${unitBlock.quantity}`,
      unitBlock.reserved ? 'Yes' : 'No',
    ];
    const columns = fixture.debugElement.queryAll(By.css('td'));
    columns.forEach((column, index) => {
      expect(columns[index].nativeElement.textContent.trim()).toEqual(
        expectedValues[index]
      );
    });
  });
});
