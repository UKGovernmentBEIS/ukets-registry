import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { RequestSubmittedComponent } from '@shared/components/account/request-submitted';

import { EmissionsTableSubmittedContainerComponent } from '.';

describe('EmissionsTableSubmittedContainerComponent', () => {
  let component: EmissionsTableSubmittedContainerComponent;
  let fixture: ComponentFixture<EmissionsTableSubmittedContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        EmissionsTableSubmittedContainerComponent,
        RequestSubmittedComponent,
      ],
      imports: [RouterTestingModule],
      providers: [provideMockStore()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(
      EmissionsTableSubmittedContainerComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
