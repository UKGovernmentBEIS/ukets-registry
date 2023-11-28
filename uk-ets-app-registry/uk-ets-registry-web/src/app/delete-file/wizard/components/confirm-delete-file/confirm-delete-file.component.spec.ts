import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmDeleteFileComponent } from './confirm-delete-file.component';

describe('ConfirmDeleteFileComponent', () => {
  let component: ConfirmDeleteFileComponent;
  let fixture: ComponentFixture<ConfirmDeleteFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfirmDeleteFileComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmDeleteFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
