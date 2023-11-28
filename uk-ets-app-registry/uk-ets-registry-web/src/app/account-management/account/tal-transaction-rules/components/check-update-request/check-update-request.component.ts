import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TalTransactionRulesRoutePaths } from '@account-management/account/tal-transaction-rules/model';
import { getRuleLabel, TrustedAccountListRules } from '@shared/model/account';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-check-update-request',
  templateUrl: './check-update-request.component.html',
  styleUrls: ['./check-update-request.component.scss'],
})
export class CheckUpdateRequestComponent implements OnInit {
  @Input()
  currentRules: TrustedAccountListRules;
  @Input()
  updatedRules: TrustedAccountListRules;
  @Input()
  isOHAOrAOHA: boolean;
  @Output() readonly updateRequestChecked = new EventEmitter();
  @Output() readonly cancel = new EventEmitter<string>();
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  talTransactionRulesRoutePaths = TalTransactionRulesRoutePaths;
  rule1Changed: boolean;
  rule2Changed: boolean;
  rule3Changed: boolean;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    if (this.currentRules.rule1 !== this.updatedRules.rule1) {
      this.rule1Changed = true;
    }
    if (this.currentRules.rule2 !== this.updatedRules.rule2) {
      this.rule2Changed = true;
    }
    if (this.currentRules.rule3 !== this.updatedRules.rule3) {
      this.rule3Changed = true;
    }
  }

  getActiveRouteUrl() {
    return this.route.snapshot['_routerState'].url;
  }

  onCancel() {
    this.cancel.emit(this.route.snapshot['_routerState'].url);
  }

  onContinue() {
    this.updateRequestChecked.emit();
  }

  navigateTo(value: TalTransactionRulesRoutePaths) {
    this.navigateToEmitter.emit(value);
  }

  getTransactionRuleValue(rule: boolean | null) {
    return getRuleLabel(rule);
  }
}
