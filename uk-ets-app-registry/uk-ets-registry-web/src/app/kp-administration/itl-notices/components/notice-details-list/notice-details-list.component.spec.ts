import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeDetailsListComponent } from './notice-details-list.component';
import { GdsDateTimeShortPipe } from '@shared/pipes';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';

describe('NoticeDetailsListComponent', () => {
  let component: NoticeDetailsListComponent;
  let fixture: ComponentFixture<NoticeDetailsListComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [
          NoticeDetailsListComponent,
          GdsDateTimeShortPipe,
          BackToTopComponent,
        ],
        providers: [provideMockStore()],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeDetailsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
