import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { TimerComponent } from './timer.component';
import { of } from 'rxjs';

describe('TimerComponent', () => {
  let component: TimerComponent;
  let fixture: ComponentFixture<TimerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TimerComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize and start the timer', fakeAsync(() => {
    const mockExpiresAt$ = of(5000);
    component.expiresAt$ = mockExpiresAt$;

    spyOn(component.expired, 'emit');

    fixture.detectChanges();
    component.ngOnInit();

    tick(5000);
    fixture.detectChanges();

    expect(component.expired.emit).toHaveBeenCalledWith(false);
  }));
});
