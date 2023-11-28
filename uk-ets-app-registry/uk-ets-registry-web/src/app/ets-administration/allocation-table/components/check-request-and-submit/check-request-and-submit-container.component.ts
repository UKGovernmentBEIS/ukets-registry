import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectAllocationTableFile } from '@allocation-table/reducers/allocation-table.selector';
import { Observable } from 'rxjs';
import { FileBase } from '@shared/model/file';
import { canGoBack, navigateTo } from '@shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import {
  cancelClicked,
  clearAllocationTableWizard,
  submitAllocationTableRequest
} from '@allocation-table/actions/allocation-table.actions';

@Component({
  selector: 'app-check-request-and-submit-container',
  template: `
    <app-check-request-and-submit
      [fileHeader]="fileHeader$ | async"
      (navigateBackEmitter)="onStepBack($event)"
      (allocationTableSubmitted)="onContinue()"
    >
    </app-check-request-and-submit>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CheckRequestAndSubmitContainerComponent implements OnInit {
  fileHeader$: Observable<FileBase>;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/ets-administration/allocation-table`
      })
    );
    this.fileHeader$ = this.store.select(selectAllocationTableFile);
  }

  onStepBack(path) {
    this.store.dispatch(clearAllocationTableWizard());
    this.store.dispatch(
      navigateTo({
        route: `/ets-administration/${path}`
      })
    );
  }

  onContinue() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(submitAllocationTableRequest());
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.activatedRoute.snapshot['_routerState'].url })
    );
  }
}
