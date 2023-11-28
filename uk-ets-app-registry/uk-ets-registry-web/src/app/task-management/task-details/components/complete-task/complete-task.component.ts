import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  CompleteTaskFormInfo,
  REQUEST_TYPE_VALUES,
  TaskDetails,
  TaskOutcome,
} from '@task-management/model';
import { Data } from '@angular/router';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { Store } from '@ngrx/store';
import * as fromAuth from '@registry-web/auth/auth.selector';
import { mergeMap } from 'rxjs/operators';
import { ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-complete-task',
  templateUrl: './complete-task.component.html',
})
export class CompleteTaskComponent implements OnInit {
  disableSubmit = false;
  _errorSummary: ErrorSummary;

  @Input()
  taskDetails: TaskDetails;
  @Input()
  data: Data;
  @Input()
  taskOutcome: TaskOutcome;
  @Input()
  set errorSummary(value: ErrorSummary) {
    this._errorSummary = value;
    this.disableSubmit = false;
  }
  @Output()
  readonly completeTaskFormInfo = new EventEmitter<CompleteTaskFormInfo>();
  @Output()
  readonly downloadErrorsCSV = new EventEmitter<number>();

  TaskOutcome = TaskOutcome;
  form: UntypedFormGroup;
  label$: Observable<string>;
  headingText: string;
  isAuthority$: Observable<boolean>;

  constructor(private formBuilder: UntypedFormBuilder, private store: Store) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      comment: [],
      otp: [],
    });
    this.headingText =
      REQUEST_TYPE_VALUES[this.taskDetails.taskType].headingText;
    this.isAuthority$ = this.store.select(fromAuth.isAuthorityUser);
    this.label$ = this.isAuthority$.pipe(
      mergeMap((isAuthority: boolean) => {
        let l =
          this.taskOutcome === TaskOutcome.APPROVED
            ? REQUEST_TYPE_VALUES[this.taskDetails.taskType].approvalText
            : REQUEST_TYPE_VALUES[this.taskDetails.taskType].rejectionText;
        if (!!isAuthority && l && l.endsWith('(optional)')) {
          l = l.substring(0, l.length - 10);
        }
        return of(l);
      })
    );
  }

  submit(): void {
    this.disableSubmit = true;
    this.completeTaskFormInfo.emit(this.form.value);
  }

  otpCodeRequired(): boolean {
    return (
      this.taskOutcome === TaskOutcome.APPROVED &&
      REQUEST_TYPE_VALUES[this.taskDetails.taskType]
        .requiresOtpVerificationOnApproval
    );
  }

  containsErrorFile(): boolean {
    return this._errorSummary?.errors?.some((e) => !!e.errorFileId);
  }

  onDownloadErrorsCSV(): void {
    this.downloadErrorsCSV.emit(this.errorFileId());
  }

  errorFileName(): string {
    return this._errorSummary?.errors?.find((e) => e.errorFileId).errorFilename;
  }

  errorFileId(): number {
    return this._errorSummary?.errors?.find((e) => e.errorFileId).errorFileId;
  }
}
