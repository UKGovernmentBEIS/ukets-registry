import { CheckUpdateRequestComponent } from '@allocation-status/components';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { DebounceClickDirective } from '@shared/debounce-click.directive';

describe('CheckUpdateRequestComponent', () => {
  let component: CheckUpdateRequestComponent;
  let fixture: ComponentFixture<CheckUpdateRequestComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, ReactiveFormsModule],
        declarations: [DebounceClickDirective, CheckUpdateRequestComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckUpdateRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it.skip('should create', () => {
    expect(component).toBeTruthy();
  });
});
