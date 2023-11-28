import {
  Component,
  ChangeDetectionStrategy,
  Input,
  OnInit,
} from '@angular/core';
import { Store } from '@ngrx/store';
import {
  ComplianceOverviewResult,
  ComplianceStatusHistoryResult,
  VerifiedEmissionsResult,
} from '@account-shared/model';
import { AccountType } from '@shared/model/account';
import { Observable } from 'rxjs';
import { AccountComplianceSelector } from '@account-management/account/account-details/store/reducers';
import { DomainEvent } from '@shared/model/event';
import { selectRegistryConfigurationProperty } from '@shared/shared.selector';

@Component({
  selector: 'app-emissions-surrenders-container',
  templateUrl: './emissions-surrenders-container.component.html',
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsSurrendersContainerComponent implements OnInit {
  @Input()
  accountId: string;

  @Input()
  accountType: AccountType;

  @Input()
  compliantEntityIdentifier: number;

  @Input()
  canRequestUpdateExclusionStatus: boolean;

  @Input()
  isAdmin: boolean;

  etsRegistryHelpEmail$: Observable<any>;
  complianceOverviewResult$: Observable<ComplianceOverviewResult>;
  verifiedEmissionsResult$: Observable<VerifiedEmissionsResult>;
  complianceStatusHistory$: Observable<ComplianceStatusHistoryResult>;
  complianceHistoryComments$: Observable<DomainEvent[]>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.etsRegistryHelpEmail$ = this.store.select(
      selectRegistryConfigurationProperty,
      {
        property: 'mail.etrAddress',
      }
    );
    this.verifiedEmissionsResult$ = this.store.select(
      AccountComplianceSelector.selectVerifiedEmissions
    );
    this.complianceStatusHistory$ = this.store.select(
      AccountComplianceSelector.selectComplianceStatusHistory
    );
    this.complianceOverviewResult$ = this.store.select(
      AccountComplianceSelector.selectComplianceOverview
    );
    if (this.isAdmin) {
      this.complianceHistoryComments$ = this.store.select(
        AccountComplianceSelector.selectComplianceHistoryComments
      );
    }
  }

  getEmissionsReportingTitle(): string {
    if (AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT === this.accountType) {
      return 'Aviation';
    } else if (AccountType.OPERATOR_HOLDING_ACCOUNT === this.accountType) {
      return 'Reportable';
    }

    throw new RangeError('This account type is not supported');
  }

  getEmissionsReportingHelpMessageTitle(): string {
    if (AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT === this.accountType) {
      return 'Help with aviation emissions';
    } else if (AccountType.OPERATOR_HOLDING_ACCOUNT === this.accountType) {
      return 'Help with reportable emissions';
    }

    throw new RangeError('This account type is not supported');
  }
}
