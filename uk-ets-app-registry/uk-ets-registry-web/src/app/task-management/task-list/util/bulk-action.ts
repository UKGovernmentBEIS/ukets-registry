import { Directive, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ErrorDetail } from 'src/app/shared/error-summary/error-detail';
import { Data, ActivatedRoute } from '@angular/router';

@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export abstract class BulkActionForm implements OnInit {
  form: UntypedFormGroup;
  requestIds: string[];
  accountIdentifiers: string[];
  taskTypes: string[];
  potentialErrors: Map<any, ErrorDetail>;

  constructor(protected route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.data.subscribe((data: Data) => {
      this.initData(data);
    });
    this.form = new UntypedFormGroup({});
  }

  submit() {
    if (this.validate()) {
      this.doSubmit();
    }
  }

  protected initData(data: Data) {
    this.requestIds = data.selectedTasks.map((task) => task.requestId);
    this.taskTypes = data.selectedTasks.map((task) => task.taskType);
    this.accountIdentifiers = Array.from(
      new Set(data.selectedTasks.map((task) => task.accountNumber))
    );
    this.potentialErrors = data.errorMap;
  }

  abstract doSubmit();

  abstract validate(): boolean;
}
