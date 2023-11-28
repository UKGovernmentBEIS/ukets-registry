import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmitEmailContainerComponent } from './submit-email-container.component';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { SubmitEmailComponent } from './submit-email.component';

import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { MemoizedSelector } from '@ngrx/store';
import { SharedState } from '@shared/shared.reducer';
import { selectResetPasswordUrlExpirationConfiguration } from '@shared/shared.selector';
import { UkProtoFormEmailComponent } from '@shared/form-controls/uk-proto-form-controls/uk-proto-form-email/uk-proto-form-email.component';

describe('SubmitEmailContainerComponent', () => {
  let component: SubmitEmailContainerComponent;
  let fixture: ComponentFixture<SubmitEmailContainerComponent>;
  let mockStore: MockStore;
  let mockResetPasswordUrlExpirationConfigurationSelector: MemoizedSelector<
    SharedState,
    number
  >;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        providers: [
          provideMockStore(),
          { provide: APP_BASE_HREF, useValue: '/' },
        ],
        declarations: [
          SubmitEmailContainerComponent,
          SubmitEmailComponent,
          UkProtoFormEmailComponent,
          BackToTopComponent,
        ],
        imports: [ReactiveFormsModule, RouterModule.forRoot([])],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SubmitEmailContainerComponent);
    component = fixture.componentInstance;
    mockStore = TestBed.inject(MockStore);
    mockResetPasswordUrlExpirationConfigurationSelector = mockStore.overrideSelector(
      selectResetPasswordUrlExpirationConfiguration,
      60
    );
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
