import { APP_BASE_HREF } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { GdsDateTimeShortPipe } from '@registry-web/shared/pipes';

import { EmissionsReportingPeriodComponent } from './emissions-reporting-period.component';

describe('EmissionsReportingPeriodComponent', () => {
  let component: EmissionsReportingPeriodComponent;
  let fixture: ComponentFixture<EmissionsReportingPeriodComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      declarations: [EmissionsReportingPeriodComponent, GdsDateTimeShortPipe],
      providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmissionsReportingPeriodComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
