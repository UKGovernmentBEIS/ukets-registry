import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { APP_BASE_HREF } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ChangePasswordConfirmationContainerComponent } from './change-password-confirmation-container.component';
import { UkPasswordInputComponent } from '@uk-password-control/components';

describe('NewPasswordFormComponent', () => {
  let component: ChangePasswordConfirmationContainerComponent;
  let fixture: ComponentFixture<ChangePasswordConfirmationContainerComponent>;
  let mockStore: MockStore<ChangePasswordConfirmationContainerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        HttpClientTestingModule,
      ],
      declarations: [
        UkPasswordInputComponent,
        ChangePasswordConfirmationContainerComponent,
      ],
      providers: [
        provideMockStore(),
        { provide: APP_BASE_HREF, useValue: '/' },
      ],
    }).compileComponents();
    mockStore = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(
      ChangePasswordConfirmationContainerComponent
    );
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(
      ChangePasswordConfirmationContainerComponent
    );
    component = fixture.debugElement.componentInstance;
    component.emailPasswordChange = 'test@test.com';
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
