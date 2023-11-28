import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeDetailsComponent } from './notice-details.component';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { GdsDateTimeShortPipe } from '@shared/pipes';
import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';

describe('NoticeDetailsComponent', () => {
  let component: NoticeDetailsComponent;
  let fixture: ComponentFixture<NoticeDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [
          NoticeDetailsComponent,
          GdsDateTimeShortPipe,
          BackToTopComponent,
        ],
        providers: [provideMockStore()],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
