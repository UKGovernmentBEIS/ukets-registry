import { APP_BASE_HREF } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { GdsDateTimePipe } from '@registry-web/shared/pipes';

import { PublicationOverviewComponent } from '@report-publication/components';

describe('PublicationOverviewComponent', () => {
  let component: PublicationOverviewComponent;
  let fixture: ComponentFixture<PublicationOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      declarations: [PublicationOverviewComponent, GdsDateTimePipe],
      providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PublicationOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
