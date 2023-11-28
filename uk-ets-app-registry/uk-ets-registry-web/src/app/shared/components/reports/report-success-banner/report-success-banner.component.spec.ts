import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportSuccessBannerComponent } from './report-success-banner.component';
import { BannerComponent } from '@shared/banner/banner.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('ReportSuccessBannerComponent', () => {
  let component: ReportSuccessBannerComponent;
  let fixture: ComponentFixture<ReportSuccessBannerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReportSuccessBannerComponent, BannerComponent],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportSuccessBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
