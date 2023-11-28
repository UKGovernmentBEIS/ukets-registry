import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadReportsListComponent } from './download-reports-list.component';
import { DownloadReportsListItemComponent } from '@reports/components';
import { RouterTestingModule } from '@angular/router/testing';
import { GdsDateTimeShortPipe } from '@shared/pipes';

describe('DownloadReportsListComponent', () => {
  let component: DownloadReportsListComponent;
  let fixture: ComponentFixture<DownloadReportsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        DownloadReportsListComponent,
        DownloadReportsListItemComponent,
        GdsDateTimeShortPipe,
      ],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadReportsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
