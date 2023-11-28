import { PortalModule } from '@angular/cdk/portal';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { GdsDateTimePipe } from '@shared/pipes';
import { FeatureHeaderWrapperComponent } from '@shared/sub-headers/header-wrapper/feature-header-wrapper.component';
import { MessageHeaderComponent } from '../message-header/message-header.component';

import { MessageDetailsContainerComponent } from './message-details-container.component';

describe('MessageDetailsContainerComponent', () => {
  let component: MessageDetailsContainerComponent;
  let fixture: ComponentFixture<MessageDetailsContainerComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, PortalModule],
        declarations: [
          MessageDetailsContainerComponent,
          FeatureHeaderWrapperComponent,
          MessageHeaderComponent,
          GdsDateTimePipe,
        ],
        providers: [provideMockStore()],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageDetailsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it.skip('should create', () => {
    expect(component).toBeTruthy();
  });
});
