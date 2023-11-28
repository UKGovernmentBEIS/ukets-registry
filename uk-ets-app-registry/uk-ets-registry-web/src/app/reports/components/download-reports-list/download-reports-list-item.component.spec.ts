import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadReportsListItemComponent } from '@reports/components';
import { GdsDateTimeShortPipe } from '@shared/pipes';
import { BannerComponent } from '@shared/banner/banner.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('DownloadReportsListItemComponent', () => {
  let component: DownloadReportsListItemComponent;
  let fixture: ComponentFixture<DownloadReportsListItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        DownloadReportsListItemComponent,
        GdsDateTimeShortPipe,
        BannerComponent,
      ],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadReportsListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
