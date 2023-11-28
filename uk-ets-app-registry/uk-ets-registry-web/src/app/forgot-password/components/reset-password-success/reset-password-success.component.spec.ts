import { APP_BASE_HREF } from '@angular/common';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';

import { ResetPasswordSuccessComponent } from './reset-password-success.component';

describe('ResetPasswordSuccessComponent', () => {
  let component: ResetPasswordSuccessComponent;
  let fixture: ComponentFixture<ResetPasswordSuccessComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ResetPasswordSuccessComponent, BackToTopComponent],
        imports: [RouterModule.forRoot([])],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetPasswordSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
