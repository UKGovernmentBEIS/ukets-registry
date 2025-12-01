import { Component, Output, EventEmitter, OnInit, Input } from '@angular/core';
import { SharedModule } from '@shared/shared.module';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { OperatorUpdateWizardPathsModel } from '@operator-update/model/operator-update-wizard-paths.model';
import { cancelClicked } from '@operator-update/actions/operator-update.actions';
import { Operator, operatorTypeMap } from '@shared/model/account';
import { Observable } from 'rxjs';
import { selectCurrentOperatorInfo } from '@operator-update/reducers';
import { AccountHolderDetailsWizardPathsModel } from '@account-management/account/account-holder-details-wizard/model';

@Component({
  standalone: true,
  imports: [SharedModule],
  selector: 'app-update-warning-page',
  templateUrl: './update-warning-page.component.html',
})
export class UpdateWarningPageComponent implements OnInit {
  @Input() updateType: string;
  operator$: Observable<Operator>;
  operatorTypeMap = operatorTypeMap;
  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.operator$ = this.store.select(selectCurrentOperatorInfo);
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onContinue() {
    if (this.updateType === 'UPDATE_OPERATOR') {
      this.router.navigate([
        `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${OperatorUpdateWizardPathsModel.BASE_PATH}`,
      ]);
    } else {
      this.router.navigate([
        `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountHolderDetailsWizardPathsModel.BASE_PATH}/${AccountHolderDetailsWizardPathsModel.UPDATE_ACCOUNT_HOLDER}`,
      ]);
    }
  }
}
