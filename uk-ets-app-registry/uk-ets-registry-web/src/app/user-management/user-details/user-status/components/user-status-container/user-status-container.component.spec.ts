import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserStatusContainerComponent } from './user-status-container.component';
import { provideMockStore } from '@ngrx/store/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('UserStatusContainerComponent', () => {
  let component: UserStatusContainerComponent;
  let fixture: ComponentFixture<UserStatusContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [UserStatusContainerComponent],
      providers: [provideMockStore()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserStatusContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
