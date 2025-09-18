import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UploadFileComponent } from './upload-file.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@registry-web/shared/shared.module';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { Configuration } from '@registry-web/shared/configuration/configuration.interface';
import * as util from '@registry-web/shared/shared.util';

describe('UploadFileComponent', () => {
  let component: UploadFileComponent;
  let fixture: ComponentFixture<UploadFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadFileComponent, ReactiveFormsModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadFileComponent);
    component = fixture.componentInstance;
    spyOn(util, 'getConfigurationValue').and.returnValue('xls');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit downloadStoredFileEmitter event when downloadStoredDocument is called', () => {
    spyOn(component.downloadStoredFileEmitter, 'emit');
    component.downloadStoredDocument();
    expect(component.downloadStoredFileEmitter.emit).toHaveBeenCalled();
  });

  it('should emit removeStoredFileEmitter event when removeStoredFile is called', () => {
    spyOn(component.removeStoredFileEmitter, 'emit');
    component.removeStoredFile();
    expect(component.removeStoredFileEmitter.emit).toHaveBeenCalled();
  });
});
