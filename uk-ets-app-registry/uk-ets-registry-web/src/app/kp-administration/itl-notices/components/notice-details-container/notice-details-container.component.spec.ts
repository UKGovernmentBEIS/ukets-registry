import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeDetailsContainerComponent } from './notice-details-container.component';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { NoticeDetailsHeaderComponent } from '@kp-administration/itl-notices/components';
import { FeatureHeaderWrapperComponent } from '@shared/sub-headers/header-wrapper/feature-header-wrapper.component';
import { GovukTagComponent } from '@shared/govuk-components';
import { PortalModule } from '@angular/cdk/portal';

xdescribe('NoticeDetailsContainerComponent', () => {
  let component: NoticeDetailsContainerComponent;
  let fixture: ComponentFixture<NoticeDetailsContainerComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          NoticeDetailsContainerComponent,
          FeatureHeaderWrapperComponent,
          NoticeDetailsHeaderComponent,
          GovukTagComponent,
        ],
        imports: [RouterTestingModule, PortalModule],
        providers: [provideMockStore()],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeDetailsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
