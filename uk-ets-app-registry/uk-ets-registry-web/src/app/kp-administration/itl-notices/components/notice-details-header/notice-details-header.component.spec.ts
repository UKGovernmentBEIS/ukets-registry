import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeDetailsHeaderComponent } from './notice-details-header.component';
import { GovukTagComponent } from '@shared/govuk-components';
import { RouterTestingModule } from '@angular/router/testing';

describe('NoticeDetailsHeaderComponent', () => {
  let component: NoticeDetailsHeaderComponent;
  let fixture: ComponentFixture<NoticeDetailsHeaderComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [NoticeDetailsHeaderComponent, GovukTagComponent],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeDetailsHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
