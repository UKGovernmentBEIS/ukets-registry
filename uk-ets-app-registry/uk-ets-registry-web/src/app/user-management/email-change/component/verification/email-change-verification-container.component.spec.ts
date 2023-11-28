import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailChangeVerificationContainerComponent } from './email-change-verification-container.component';
import { EmailChangeVerificationComponent } from '@email-change/component';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { EmailChangeState } from '@email-change/reducer';
import { RouterTestingModule } from '@angular/router/testing';

describe('EmailChangeVerificationContainerComponent', () => {
  let component: EmailChangeVerificationContainerComponent;
  let fixture: ComponentFixture<EmailChangeVerificationContainerComponent>;
  let mockStore: MockStore<EmailChangeState>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        declarations: [
          EmailChangeVerificationContainerComponent,
          EmailChangeVerificationComponent,
        ],
        providers: [provideMockStore()],
      }).compileComponents();
      mockStore = TestBed.inject(MockStore);
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(
      EmailChangeVerificationContainerComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
