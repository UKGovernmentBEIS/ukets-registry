import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationRendererComponent } from './notification-renderer.component';

describe('NotificationRendererComponent', () => {
  let component: NotificationRendererComponent;
  let fixture: ComponentFixture<NotificationRendererComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotificationRendererComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationRendererComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
