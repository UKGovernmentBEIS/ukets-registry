import { Component, Input } from '@angular/core';
import { AcquiringAccountInfo } from '@shared/model/transaction';

@Component({
  selector: 'app-nat-and-ner-allocation-details-table',
  templateUrl: './nat-and-ner-allocation-details-table.component.html',
  styleUrls: ['./nat-and-ner-allocation-details-table.component.scss'],
})
export class NatAndNerAllocationDetailsTableComponent {
  @Input()
  isEtsTransaction: boolean;

  @Input()
  natAmount: number;

  @Input()
  nerAmount: number;

  @Input()
  totalOverAllocatedQuantity: number;

  @Input()
  natAcquiringAccountInfo: AcquiringAccountInfo;

  @Input()
  nerAcquiringAccountInfo: AcquiringAccountInfo;

  @Input()
  natTransactionIdentifier: string;

  @Input()
  nerTransactionIdentifier: string;
}
