import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  selectRegistryLevelInfoList,
  selectSelectedQuantity,
  selectSelectedRegistryLevel
} from '../../reducers';
import { Observable } from 'rxjs';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { RegistryLevelInfo } from '@shared/model/transaction';
import { IssueKpUnitsActions } from '@issue-kp-units/actions';
import { IssueKpUnitsRoutePathsModel } from '@issue-kp-units/model';

@Component({
  selector: 'app-select-unit-type-container-component',
  template: `
    <app-select-unit-type-component
      [registryLevelInfos]="registryLevelInfoList$ | async"
      [selectedQuantity]="selectedQuantity$ | async"
      [selectedRegistryLevelInfo]="selectedRegistryInfo$ | async"
      (selectedRegistryLevelAndQuantity)="
        onSelectRegistryLevelAndQuantity($event)
      "
      (errorDetails)="onError($event)"
    >
    </app-select-unit-type-component>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: []
})
export class SelectUnitTypeContainerComponent implements OnInit {
  registryLevelInfoList$: Observable<RegistryLevelInfo[]>;
  selectedQuantity$: Observable<number>;
  selectedRegistryInfo$: Observable<RegistryLevelInfo>;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/kpadministration/issuekpunits/${IssueKpUnitsRoutePathsModel['select-commitment-period']}`
      })
    );

    this.store.dispatch(IssueKpUnitsActions.loadRegistryLevels());
    this.registryLevelInfoList$ = this.store.select(
      selectRegistryLevelInfoList
    );
    this.selectedQuantity$ = this.store.select(selectSelectedQuantity);
    this.selectedRegistryInfo$ = this.store.select(selectSelectedRegistryLevel);
  }

  onSelectRegistryLevelAndQuantity($event: {
    registryLevelInfo: RegistryLevelInfo;
    quantity: number;
  }) {
    this.store.dispatch(
      IssueKpUnitsActions.selectRegistryLevelAndQuantity({
        selectedRegistryLevel: $event.registryLevelInfo,
        quantity: $event.quantity
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
