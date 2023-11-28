import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  AccountInfo,
  CommitmentPeriod,
  RegistryLevelInfo,
} from '@shared/model/transaction';
import { IssueKpUnitsRoutePathsModel } from '@issue-kp-units/model';

@Component({
  selector: 'app-check-request-and-submit',
  templateUrl: './check-request-and-sign.component.html',
})
export class CheckRequestAndSignComponent {
  public IssueKpUnitsRoutePathsModel = IssueKpUnitsRoutePathsModel;

  otpCode: string;

  @Input()
  selectedCommitmentPeriod: CommitmentPeriod;

  @Input()
  selectedAcquiringAccount: AccountInfo;

  @Input()
  selectedRegistryLevel: RegistryLevelInfo;

  @Input()
  selectedQuantity: number;

  @Input()
  transactionId: string;

  @Input()
  transactionReference: string;

  @Output() readonly otpCodeEmitter = new EventEmitter<string>();

  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  submit() {
    this.otpCodeEmitter.emit(this.otpCode);
  }

  setOtpCode(otpCode: string) {
    this.otpCode = otpCode;
  }

  navigateTo(value: IssueKpUnitsRoutePathsModel) {
    this.navigateToEmitter.emit(value);
  }
}
