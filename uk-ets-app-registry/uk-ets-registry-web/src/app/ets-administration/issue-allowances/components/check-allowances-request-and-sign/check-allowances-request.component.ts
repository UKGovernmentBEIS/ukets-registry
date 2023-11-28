import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  AcquiringAccountInfo,
  AllowanceTransactionBlockSummary,
} from '@shared/model/transaction';
import { empty } from '@shared/shared.util';

@Component({
  selector: 'app-check-allowances-request',
  templateUrl: './check-allowances-request.component.html',
})
export class CheckAllowancesRequestComponent {
  otpCode: string;

  @Input()
  transactionBlockSummaries: Partial<AllowanceTransactionBlockSummary>[];

  @Input()
  acquiringAccountInfo: AcquiringAccountInfo;

  @Input()
  transactionId: string;

  @Input()
  transactionReference: string;

  @Output() readonly otpCodeEmitter = new EventEmitter<string>();

  /**
   * the currentBlock should have the quantity set from the first step.
   */
  getCurrentBlock(): Partial<AllowanceTransactionBlockSummary> {
    return this.transactionBlockSummaries.find((a) => a.quantity != null);
  }
  getActiveYear(): number {
    return this.getCurrentBlock().year;
  }

  quantityYearWarning(): boolean {
    return +this.getCurrentBlock().quantity > this.getCurrentBlock().remaining;
  }

  quantityPhaseWarning(): boolean {
    const remaining =
      this.transactionBlockSummaries !== undefined
        ? this.transactionBlockSummaries.reduce((a, b) => a + b['remaining'], 0)
        : 0;

    return Number(this.getCurrentBlock().quantity) > remaining;
  }

  submit() {
    this.otpCodeEmitter.emit(this.otpCode);
  }

  setOtpCode(otpCode: string) {
    this.otpCode = otpCode;
  }
}
