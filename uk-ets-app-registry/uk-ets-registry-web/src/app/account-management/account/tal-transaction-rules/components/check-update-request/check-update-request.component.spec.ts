import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckUpdateRequestComponent } from './check-update-request.component';
import { APP_BASE_HREF } from '@angular/common';
import { RouterModule } from '@angular/router';

describe('CheckUpdateRequestComponent', () => {
  let component: CheckUpdateRequestComponent;
  let fixture: ComponentFixture<CheckUpdateRequestComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterModule.forRoot([])],
        declarations: [CheckUpdateRequestComponent],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckUpdateRequestComponent);
    component = fixture.componentInstance;
    component.currentRules = {
      rule1: true,
      rule2: false,
      rule3: true,
    };
    component.updatedRules = {
      rule1: true,
      rule2: false,
      rule3: true,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
