import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NotificationsEmailUploadFileComponent } from '@notifications/notifications-wizard/component/notifications-email-upload-file/notifications-email-upload-file.component';
import { provideMockStore } from '@ngrx/store/testing';
import { UkSelectFileComponent } from '@shared/form-controls';
import { By } from '@angular/platform-browser';

describe('NotificationsResultsComponent', () => {
  let component: NotificationsEmailUploadFileComponent;
  let fixture: ComponentFixture<NotificationsEmailUploadFileComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
      declarations: [
        NotificationsEmailUploadFileComponent,
        UkSelectFileComponent,
      ],
      providers: [provideMockStore()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationsEmailUploadFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('File Selection', () => {
    it('should update form value when a file is selected', () => {
      const mockFile = new File([''], 'test.xlsx', {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      });
      const fileList = {
        0: mockFile,
        length: 1,
        item: (index: number) => mockFile,
      } as unknown as FileList;

      const fileInput = fixture.debugElement.query(
        By.css('input[type="file"]')
      );

      component.onFileSelected(fileList);
      fixture.detectChanges();

      expect(component.formGroup.get('emailRecipients')?.value).toEqual(
        mockFile
      );
    });
  });

  describe('Cancel Functionality', () => {
    it('should emit the cancel event when onCancel is called', () => {
      spyOn(component.cancelEmitter, 'emit');

      component.onCancel();
      fixture.detectChanges();

      expect(component.cancelEmitter.emit).toHaveBeenCalled();
    });
  });
});
