import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectDocumentsRequestType,
  selectOriginatingPath,
  selectParentRequestId,
  selectRequestDocumentsOrigin,
  selectSubmittedRequestIdentifier,
} from '@request-documents/wizard/reducers/request-document.selector';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { canGoBack } from '@shared/shared.action';
import { Router } from '@angular/router';
import { TaskDetailsActions } from '@task-details/actions';

@Component({
  selector: 'app-documents-request-submitted-container',
  template: `
    <app-documents-request-submitted
      [submittedRequestIdentifier]="submittedRequestIdentifier$ | async"
      [documentsRequestType]="documentsRequestType$ | async"
      [parentRequestId]="parentRequestId$ | async"
      [originatingPath]="originatingPath$ | async"
      [origin]="origin$ | async"
      (navigateToEmitter)="navigateTo($event)"
    ></app-documents-request-submitted>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentsRequestSubmittedContainerComponent implements OnInit {
  submittedRequestIdentifier$: Observable<string>;
  parentRequestId$: Observable<string>;
  documentsRequestType$: Observable<DocumentsRequestType>;
  originatingPath$: Observable<string>;
  origin$: Observable<RequestDocumentsOrigin>;

  constructor(private store: Store, private router: Router) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.submittedRequestIdentifier$ = this.store.select(
      selectSubmittedRequestIdentifier
    );
    this.documentsRequestType$ = this.store.select(selectDocumentsRequestType);
    this.originatingPath$ = this.store.select(selectOriginatingPath);
    this.origin$ = this.store.select(selectRequestDocumentsOrigin);
    this.parentRequestId$ = this.store.select(selectParentRequestId);
  }

  navigateTo(info) {
    if (
      info.origin === RequestDocumentsOrigin.USER ||
      info.origin === RequestDocumentsOrigin.ACCOUNT_DETAILS
    ) {
      this.router.navigate([info.path]);
    } else {
      /* We perform this dispatch because we want to reload the parent task data with the updated sub-task list*/
      this.store.dispatch(
        TaskDetailsActions.loadTaskFromList({ taskId: info.taskRequestId })
      );
    }
  }
}
