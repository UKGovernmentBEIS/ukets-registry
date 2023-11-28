import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Store, StoreModule } from '@ngrx/store';

import { BannerComponent } from '@shared/banner/banner.component';
import { RouterTestingModule } from '@angular/router/testing';
import { StandardReportsComponent, StandardReportsFiltersComponent } from '@reports/components';
import { ReportSuccessBannerComponent } from '@shared/components/reports';
import { provideMockStore } from '@ngrx/store/testing';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { TypeAheadComponent } from '@registry-web/shared/form-controls/type-ahead/type-ahead.component';
import { TypeAheadService } from '@registry-web/shared/services';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('StandardReportComponent', () => {
  let component: StandardReportsComponent;
  let fixture: ComponentFixture<StandardReportsComponent>;
  let store: Store;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [
        StandardReportsComponent,
        ReportSuccessBannerComponent,
        BannerComponent,
        TypeAheadComponent,
        StandardReportsFiltersComponent
      ],
      imports: [StoreModule.forRoot({}), HttpClientModule, NgbTypeaheadModule, ReactiveFormsModule, FormsModule, RouterTestingModule],
      providers: [provideMockStore(), TypeAheadService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StandardReportsComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(Store);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
