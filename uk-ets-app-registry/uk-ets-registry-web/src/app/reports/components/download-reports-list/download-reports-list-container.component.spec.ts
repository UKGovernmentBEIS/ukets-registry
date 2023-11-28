import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadReportsListContainerComponent } from './download-reports-list-container.component';
import { Store, StoreModule } from '@ngrx/store';
import {
  DownloadReportsListComponent,
  DownloadReportsListItemComponent,
} from '@reports/components';
import { provideMockStore } from '@ngrx/store/testing';
import { BannerComponent } from '@shared/banner/banner.component';
import { RouterTestingModule } from '@angular/router/testing';
import { GdsDateTimeShortPipe } from '@shared/pipes';

describe('DownloadReportsListContainerComponent', () => {
  let component: DownloadReportsListContainerComponent;
  let fixture: ComponentFixture<DownloadReportsListContainerComponent>;
  let store: Store;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({}), RouterTestingModule],
      declarations: [
        DownloadReportsListContainerComponent,
        DownloadReportsListComponent,
        DownloadReportsListItemComponent,
        BannerComponent,
        GdsDateTimeShortPipe,
      ],
      providers: [provideMockStore()],
    });

    await TestBed.compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadReportsListContainerComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(Store);

    spyOn(store, 'dispatch').and.callThrough();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
