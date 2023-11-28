import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CheckAndConfirmAddNoteComponent } from './check-and-confirm-add-note.component';
import { SharedModule } from '@registry-web/shared/shared.module';

describe('CheckAndConfirmAddNoteComponent', () => {
  let component: CheckAndConfirmAddNoteComponent;
  let fixture: ComponentFixture<CheckAndConfirmAddNoteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [CheckAndConfirmAddNoteComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckAndConfirmAddNoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit handleCancel when cancel link is clicked', () => {
    spyOn(component.handleCancel, 'emit');
    const cancelButton = fixture.nativeElement.querySelector(
      '.govuk-link--no-visited-state'
    );
    cancelButton.click();
    expect(component.handleCancel.emit).toHaveBeenCalled();
  });

  it(
    'should emit handleSubmit when submit button is clicked',
    waitForAsync(() => {
      spyOn(component.handleSubmit, 'emit');
      const submitButton = fixture.nativeElement.querySelector('button');
      submitButton.click();
      setTimeout(
        () => expect(component.handleSubmit.emit).toHaveBeenCalled(),
        250
      );
    })
  );

  it('should emit handleChange when change link is clicked', () => {
    spyOn(component.handleChange, 'emit');
    const changeLink = fixture.nativeElement.querySelector(
      '.govuk-summary-list__actions a'
    );
    changeLink.click();
    expect(component.handleChange.emit).toHaveBeenCalled();
  });
});
