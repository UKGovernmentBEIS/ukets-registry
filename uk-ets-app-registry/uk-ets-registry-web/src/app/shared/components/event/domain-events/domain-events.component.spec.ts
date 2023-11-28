import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { DomainEventsComponent } from './domain-events.component';
import { By } from '@angular/platform-browser';
import { EventTypePipe, GdsDateTimeShortPipe } from '@shared/pipes';
import { RouterTestingModule } from '@angular/router/testing';

describe('DomainEventsComponent', () => {
  let component: DomainEventsComponent;
  let fixture: ComponentFixture<DomainEventsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          DomainEventsComponent,
          GdsDateTimeShortPipe,
          EventTypePipe,
        ],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(DomainEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('table headers are rendered correctly', () => {
    component.domainEvents = [
      {
        domainType: 'gov.uk.ets.registry.api.transaction.domain.Transaction',
        domainId: 'GB100020',
        domainAction: 'Transaction proposal submitted (comment)',
        description: 'User entered comment',
        creator: 'UK802061511788',
        creatorType: 'user',
        creationDate: new Date('2020-05-29T09:19:00'),
      },
    ];
    fixture.detectChanges();
    let key = fixture.debugElement.queryAll(By.css('.govuk-table__header'))[0];
    expect(key.nativeElement.textContent).toContain('When');
    key = fixture.debugElement.queryAll(By.css('.govuk-table__header'))[1];
    expect(key.nativeElement.textContent).toContain('Who');
    key = fixture.debugElement.queryAll(By.css('.govuk-table__header'))[2];
    expect(key.nativeElement.textContent).toContain('What');
    key = fixture.debugElement.queryAll(By.css('.govuk-table__header'))[3];
    expect(key.nativeElement.textContent).toContain('Request ID');
    key = fixture.debugElement.queryAll(By.css('.govuk-table__header'))[4];
    expect(key.nativeElement.textContent).toContain('Comments');
  });

  test('table data are rendered correctly', () => {
    component.domainEvents = [
      {
        domainType: 'gov.uk.ets.registry.api.transaction.domain.Transaction',
        domainId: 'GB100020',
        domainAction: 'Transaction proposal submitted (comment)',
        description: 'User entered comment',
        creator: 'UK802061511788',
        creatorType: 'user',
        creationDate: new Date('2020-05-29T09:19:00'),
      },
    ];
    fixture.detectChanges();
    let key = fixture.debugElement.queryAll(By.css('.govuk-table__cell'))[0];
    expect(key.nativeElement.textContent).toContain('29 May 2020, 9:19am');
    key = fixture.debugElement.queryAll(By.css('.govuk-table__cell'))[1];
    expect(key.nativeElement.textContent).toContain('UK802061511788');
    key = fixture.debugElement.queryAll(By.css('.govuk-table__cell'))[2];
    expect(key.nativeElement.textContent).toContain(
      'Transaction proposal submitted (comment)'
    );
    key = fixture.debugElement.queryAll(By.css('.govuk-table__cell'))[3];
    expect(key.nativeElement.textContent).toContain('GB100020');
    key = fixture.debugElement.queryAll(By.css('.govuk-table__cell'))[4];
    expect(key.nativeElement.textContent).toContain('User entered comment');
  });
});
