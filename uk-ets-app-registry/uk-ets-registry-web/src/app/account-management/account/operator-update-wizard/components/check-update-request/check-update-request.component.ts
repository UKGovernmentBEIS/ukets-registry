import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  InstallationActivityType,
  Operator,
  OperatorType,
  operatorTypeMap,
} from '@shared/model/account';
import { ErrorDetail } from '@shared/error-summary';
import { OperatorUpdate } from '@operator-update/model/operator-update';
import { OperatorUpdateWizardPathsModel } from '@operator-update/model/operator-update-wizard-paths.model';

@Component({
  selector: 'app-check-update-request',
  templateUrl: `./check-update-request.component.html`,
  styleUrls: ['./check-update-request.component.scss'],
})
export class CheckUpdateRequestComponent implements OnInit {
  @Input()
  newOperatorInfo: Operator;
  @Input()
  currentOperatorInfo: Operator;
  @Output() readonly cancelEmitter = new EventEmitter();
  @Output() readonly submitRequest = new EventEmitter<OperatorUpdate>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail>();
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  isInstallation: boolean;
  isAircraft: boolean;
  titleOfIdentifier: string;
  activityTypes = InstallationActivityType;
  changedValues = {};
  routePathForDetails: string;
  operatorTypeMap = operatorTypeMap;

  ngOnInit() {
    this.isInstallation =
      this.currentOperatorInfo.type === OperatorType.INSTALLATION;
    this.isAircraft =
      this.currentOperatorInfo.type === OperatorType.AIRCRAFT_OPERATOR;
    if (this.currentOperatorInfo.type === OperatorType.AIRCRAFT_OPERATOR) {
      this.titleOfIdentifier = 'Aircraft Operator ID';
    }
    if (this.currentOperatorInfo.type === OperatorType.INSTALLATION) {
      this.titleOfIdentifier = 'Installation ID';
    }
    this.changedValues = this.getObjectDiff(
      this.currentOperatorInfo,
      this.newOperatorInfo
    );

    this.routePathForDetails = OperatorUpdateWizardPathsModel.BASE_PATH;
  }

  onCancel() {
    this.cancelEmitter.emit();
  }

  getObjectDiff(current, changed) {
    if (this.isInstallation) {
      let permit = null;
      if (
        Object.entries(
          this.getOnlyChangedValues(current, changed, 'permit', false)
        ).length > 0
      ) {
        permit = {
          permit: this.getOnlyChangedValues(current, changed, 'permit', false),
        };
      }
      return {
        ...permit,
        ...this.getOnlyChangedValues(current, changed, 'firstYear', false),
        ...this.getOnlyChangedValues(current, changed, 'lastYear', true),
        ...this.getOnlyChangedValues(current, changed, 'name', false),
        ...this.getOnlyChangedValues(current, changed, 'activityType', false),
        ...this.getOnlyChangedValues(current, changed, 'regulator', false),
      };
    } else if (this.isAircraft) {
      let monitoringPlan = null;
      if (
        Object.entries(
          this.getOnlyChangedValues(current, changed, 'monitoringPlan', false)
        ).length > 0
      ) {
        monitoringPlan = {
          monitoringPlan: this.getOnlyChangedValues(
            current,
            changed,
            'monitoringPlan',
            false
          ),
        };
      }
      return {
        ...monitoringPlan,
        ...this.getOnlyChangedValues(current, changed, 'firstYear', false),
        ...this.getOnlyChangedValues(current, changed, 'lastYear', true),
        ...this.getOnlyChangedValues(current, changed, 'regulator', false),
      };
    }
  }

  private getOnlyChangedValues(
    initialValues,
    updatedValues,
    property,
    isOptional
  ) {
    const diff = {};
    if (updatedValues[property] || isOptional) {
      if (
        initialValues[property] &&
        typeof initialValues[property] === 'object'
      ) {
        Object.keys(updatedValues[property]).forEach((r) => {
          if (updatedValues[property][r] != initialValues[property][r]) {
            diff[r] = updatedValues[property][r];
          }
        });
      } else {
        if (property === 'lastYear' && updatedValues[property] === '') {
          updatedValues[property] = null;
        }
        if (initialValues[property] != updatedValues[property]) {
          diff[property] = updatedValues[property];
          if (property === 'lastYear') {
            diff['lastYearChanged'] = true;
          }
        }
      }
    }
    return diff;
  }

  onSubmit() {
    if (!Object.keys(this.changedValues).length) {
      this.errorDetails.emit(
        new ErrorDetail(null, 'You can not make a request without any changes.')
      );
    } else {
      this.submitRequest.emit({
        type: this.currentOperatorInfo.type,
        name: this.changedValues['name'],
        activityType: this.changedValues['activityType'],
        monitoringPlan: this.changedValues['monitoringPlan'],
        permit: this.setPermitValues(),
        regulator: this.currentOperatorInfo.regulator,
        changedRegulator: this.changedValues['regulator'],
        firstYear: this.changedValues['firstYear'],
        lastYear: this.changedValues['lastYear'],
        lastYearChanged: this.changedValues['lastYearChanged'],
      });
    }
  }

  private setPermitValues() {
    let permitObj = null;
    if (this.changedValues['permit']) {
      permitObj = {};
      if (this.changedValues['permit']['id']) {
        permitObj['id'] = this.changedValues['permit']['id'];
      } else {
        permitObj['id'] = this.currentOperatorInfo['permit']['id'];
      }

      if (this.changedValues['permit']['date']) {
        permitObj['date'] = this.changedValues['permit']['date'];
      } else {
        permitObj['date'] = this.currentOperatorInfo['permit']['date'];
      }
    }
    return permitObj;
  }

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }
}
