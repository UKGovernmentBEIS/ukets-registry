import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { StandardReportsContainerComponent } from '@reports/components';
import { Store, StoreModule } from '@ngrx/store';
import { StandardReportsComponent } from '@reports/components';
import { provideMockStore } from '@ngrx/store/testing';
import { BannerComponent } from '@shared/banner/banner.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReportSuccessBannerComponent } from '@shared/components/reports';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('StandardReportsListContainerComponent', () => {
  let component: StandardReportsContainerComponent;
  let fixture: ComponentFixture<StandardReportsContainerComponent>;
  let store: Store;

  beforeEach(
    waitForAsync(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [StoreModule.forRoot({}), RouterTestingModule],
      declarations: [
        StandardReportsContainerComponent,
        StandardReportsComponent,
        ReportSuccessBannerComponent,
        BannerComponent,
      ],
      providers: [provideMockStore()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StandardReportsContainerComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(Store);

    spyOn(store, 'dispatch').and.callThrough();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
