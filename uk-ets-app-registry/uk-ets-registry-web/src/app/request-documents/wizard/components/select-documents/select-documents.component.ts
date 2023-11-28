import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  AbstractControl,
  UntypedFormBuilder,
  UntypedFormControl,
} from '@angular/forms';

@Component({
  selector: 'app-select-documents',
  templateUrl: './select-documents.component.html',
})
export class SelectDocumentsComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  accountNumber: string;
  @Input()
  accountName: string;
  @Input()
  accountHolderName: string;
  @Input()
  recipientName: string;
  @Input()
  documentNames: string[];
  @Output() readonly setDocumentNames = new EventEmitter<string[]>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    const formControls =
      this.documentNames.length > 0
        ? this.documentNames.map((documentName) =>
            this.buildDocumentNameControl(documentName)
          )
        : [1, 2, 3].map(() => this.buildDocumentNameControl(''));

    formControls.forEach((control, index) =>
      this.formGroup.addControl(index.toString(), control)
    );
  }

  protected doSubmit() {
    this.documentNames = Object.keys(this.formGroup.controls).map((key) => {
      return this.formGroup.controls[key].value;
    });

    this.setDocumentNames.emit(
      this.documentNames.filter((documentName) => documentName !== '')
    );
  }

  protected getFormModel(): any {
    return {};
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }

  getTitleText(): string {
    return this.accountHolderName
      ? 'Request account holder documents'
      : 'Request user documents';
  }

  getAllFormControls(): AbstractControl[] {
    return Object.keys(this.formGroup.controls).map(
      (key) => this.formGroup.controls[key]
    );
  }

  removeFormControl(control: AbstractControl) {
    this.formGroup.removeControl(this.getControlName(control));
  }

  private getNextIndex() {
    const objectKeys = Object.keys(this.formGroup.controls);
    if (objectKeys.length > 0) {
      return (
        Math.max(
          ...Object.keys(this.formGroup.controls).map((key) => Number(key))
        ) + 1
      );
    } else {
      return 0;
    }
  }

  private addForm(index: string, documentName: string) {
    const objectKeys = Object.keys(this.formGroup.controls);
    if (objectKeys.length < 10) {
      this.formGroup.addControl(
        index,
        this.buildDocumentNameControl(documentName)
      );
    }
  }

  buildDocumentNameControl(documentName: string): UntypedFormControl {
    return new UntypedFormControl(documentName);
  }

  addFormControl() {
    this.addForm(this.getNextIndex().toString(), '');
  }

  getControlName(c: AbstractControl): string | null {
    const formGroup = c.parent.controls;
    return Object.keys(formGroup).find((name) => c === formGroup[name]) || null;
  }
}
