import { Component, Input } from '@angular/core';
import {
  ComplianceStatusHistoryResult,
  complianceStatusMap,
} from '@account-shared/model';

@Component({
  selector: 'app-compliance-status-history',
  templateUrl: './compliance-status-history.component.html',
  styles: [],
})
export class ComplianceStatusHistoryComponent {
  @Input()
  history: ComplianceStatusHistoryResult;
  complianceStatusMap = complianceStatusMap;
}
