import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { MenuItemEnum } from '@registry-web/account-management/account/account-details/model';
import {
  AircraftOperator,
  Installation,
  Operator,
  OperatorType,
} from '@registry-web/shared/model/account';
import { AccountInfo } from '@shared/model/transaction';

@Component({
  selector: 'app-account-summary',
  templateUrl: './account-summary.component.html',
  styleUrls: ['./account-summary.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountSummaryComponent {
  @Input()
  header: string;
  @Input()
  hasLink: boolean;
  @Input()
  tip: string;
  @Input()
  accountInfo: AccountInfo;
  @Input()
  hideTip: boolean;
  // Introduced for UKETS-3416
  @Input()
  hideAccountHolderName: boolean;
  // Introduced for UKETS-3416
  @Input()
  showDescription = true;
  @Input()
  showAccountNameInsteadOfNumber: boolean;
  @Input()
  showAccountNameOrDescriptionLabel = true;
  @Input()
  hideAccountNumber: boolean;
  @Input()
  isTaskBased: boolean;
  @Input()
  hasAccountAccess: boolean;
  @Input()
  isTransactionReversal: boolean;
  @Input()
  selectedSideMenu?: MenuItemEnum;
  @Input()
  operator?: Installation | AircraftOperator | any;

  @Output() readonly linkClicked = new EventEmitter<void>();

  OperatorType = OperatorType;
}
