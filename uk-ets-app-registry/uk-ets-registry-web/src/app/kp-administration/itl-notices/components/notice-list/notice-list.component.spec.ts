import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeListComponent } from './notice-list.component';
import { GovukTagComponent } from '@shared/govuk-components';
import { GdsDateTimeShortPipe } from '@shared/pipes';
import { RouterTestingModule } from '@angular/router/testing';

describe('NoticeListComponent', () => {
  let component: NoticeListComponent;
  let fixture: ComponentFixture<NoticeListComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          NoticeListComponent,
          GovukTagComponent,
          GdsDateTimeShortPipe,
        ],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
