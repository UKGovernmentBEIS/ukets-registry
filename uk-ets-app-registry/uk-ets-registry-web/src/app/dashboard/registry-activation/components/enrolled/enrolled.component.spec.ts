import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { EnrolledComponent } from '@registry-web/dashboard/registry-activation/components/enrolled/enrolled.component';
import { provideMockStore } from '@ngrx/store/testing';

describe('EnrolledComponent', () => {
  let component: EnrolledComponent;
  let fixture: ComponentFixture<EnrolledComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [EnrolledComponent],
        providers: [
          { provide: APP_BASE_HREF, useValue: '/' },
          provideMockStore(),
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(EnrolledComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
