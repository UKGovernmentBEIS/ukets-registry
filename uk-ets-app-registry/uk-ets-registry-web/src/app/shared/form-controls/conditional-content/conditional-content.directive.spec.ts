import { Component, QueryList, ViewChildren } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { ConditionalContentDirective } from './conditional-content.directive';

describe('ConditionalContentDirective', () => {
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;

  @Component({
    standalone: true,
    imports: [ReactiveFormsModule, ConditionalContentDirective],
    template: `
      <form [formGroup]="form">
        <div appConditionalContent>
          <input type="text" formControlName="first" />

          <div appConditionalContent>
            <input type="text" formControlName="second" />
            <div formGroupName="nestedGroup"></div>
          </div>

          <div formGroupName="group">
            <div appConditionalContent>
              <input type="text" formControlName="third" />
            </div>
            <input type="text" formControlName="fourth" />
          </div>
        </div>
      </form>
    `,
  })
  class TestComponent {
    @ViewChildren(ConditionalContentDirective)
    conditionals: QueryList<ConditionalContentDirective>;

    form = new FormGroup({
      first: new FormControl(),
      second: new FormControl(),
      group: new FormGroup({
        third: new FormControl(),
        fourth: new FormControl(),
      }),
      nestedGroup: new FormGroup({}),
    });
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create an instance', () => {
    expect(
      fixture.debugElement.queryAll(By.directive(ConditionalContentDirective))
    ).toHaveLength(3);
  });

  it('should disable all nested controls and groups', () => {
    hostComponent.conditionals.get(0).disableControls();
    fixture.detectChanges();

    expect(hostComponent.form.get('first').disabled).toBeTruthy();
    expect(hostComponent.form.get('second').disabled).toBeTruthy();
    expect(hostComponent.form.get(['group', 'third']).disabled).toBeTruthy();
    expect(hostComponent.form.get(['group', 'fourth']).disabled).toBeTruthy();
    expect(hostComponent.form.get('group').disabled).toBeTruthy();
    expect(hostComponent.form.get('nestedGroup').disabled).toBeTruthy();
  });

  it('should enable nested controls', () => {
    hostComponent.form.disable();
    fixture.detectChanges();

    hostComponent.conditionals.get(0).enableControls();
    fixture.detectChanges();

    expect(hostComponent.form.get('first').disabled).toBeFalsy();
    expect(hostComponent.form.get('second').disabled).toBeTruthy();
    expect(hostComponent.form.get('group').disabled).toBeFalsy();
    expect(hostComponent.form.get(['group', 'third']).disabled).toBeTruthy();
    expect(hostComponent.form.get(['group', 'fourth']).disabled).toBeFalsy();
    expect(hostComponent.form.get('nestedGroup').disabled).toBeTruthy();

    hostComponent.conditionals.get(1).enableControls();
    fixture.detectChanges();

    expect(hostComponent.form.get('second').disabled).toBeFalsy();
    expect(hostComponent.form.get(['group', 'third']).disabled).toBeTruthy();
  });
});
