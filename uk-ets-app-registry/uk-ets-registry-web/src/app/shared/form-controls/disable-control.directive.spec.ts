import { DisableControlDirective } from './disable-control.directive';
import { Component } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UkProtoFormTextComponent } from '@shared/form-controls/uk-proto-form-controls';
import { By } from '@angular/platform-browser';

describe('DisableControlDirective', () => {
  let directive: DisableControlDirective;
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    selector: 'app-test-component',
    template: `
      <form [formGroup]="form">
        <input [formControl]="testControl" [appDisableControl]="disabled" />
      </form>
    `,
  })
  class TestComponent {
    testControl = new FormControl();
    form = this.fb.group([this.testControl]);

    disabled = false;

    constructor(private readonly fb: FormBuilder) {}
  }

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [
        TestComponent,
        DisableControlDirective,
        UkProtoFormTextComponent,
      ],
      imports: [ReactiveFormsModule],
    }).createComponent(TestComponent);

    component = fixture.componentInstance;
    fixture.detectChanges();
    directive = fixture.debugElement
      .query(By.directive(DisableControlDirective))
      .injector.get(DisableControlDirective);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should disabled input', () => {
    component.disabled = true;
    fixture.detectChanges();

    const input = fixture.nativeElement.querySelector('input');
    expect(input.getAttribute('disabled')).toEqual('');
  });

  it('should enable input', () => {
    component.disabled = true;
    fixture.detectChanges();

    const input = fixture.nativeElement.querySelector('input');
    expect(input.getAttribute('disabled')).toEqual('');

    component.disabled = false;
    fixture.detectChanges();

    expect(input.getAttribute('disabled')).toBeNull();
  });
});
