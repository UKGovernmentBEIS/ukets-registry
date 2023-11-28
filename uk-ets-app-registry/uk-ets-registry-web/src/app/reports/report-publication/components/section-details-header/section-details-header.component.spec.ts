import { APP_BASE_HREF } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { GdsDateTimePipe } from '@registry-web/shared/pipes';

import { SectionDetailsHeaderComponent } from './section-details-header.component';

describe('SectionDetailsHeaderComponent', () => {
  let component: SectionDetailsHeaderComponent;
  let fixture: ComponentFixture<SectionDetailsHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      declarations: [SectionDetailsHeaderComponent, GdsDateTimePipe],
      providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SectionDetailsHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
