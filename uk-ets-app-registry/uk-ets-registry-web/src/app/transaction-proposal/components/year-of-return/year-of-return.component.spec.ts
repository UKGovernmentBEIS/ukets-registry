import { ComponentFixture, TestBed } from '@angular/core/testing';

import { YearOfReturnComponent } from './year-of-return.component';

describe('YearOfReturnComponent', () => {
  let component: YearOfReturnComponent;
  let fixture: ComponentFixture<YearOfReturnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [YearOfReturnComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(YearOfReturnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
