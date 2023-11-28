import { Component, Input, Output, EventEmitter } from '@angular/core';
import { AuthoriseRepresentativeTaskDetails } from '@task-management/model';

@Component({
  selector: 'app-authorise-representatives-update-task-details-container',
  template: `
    <app-authorise-representatives-update-task-details
      [authoriseRepresentativeTaskDetails]="taskDetails"
      (requestDocumentEmitter)="onUserRequestDocuments($event)"
    ></app-authorise-representatives-update-task-details>
  `,
})
export class AuthorisedRepresentativesUpdateTaskDetailsContainerComponent {
  @Input()
  taskDetails: AuthoriseRepresentativeTaskDetails;

  @Output() readonly requestDocumentEmitter = new EventEmitter();

  onUserRequestDocuments(requestDocumentDetails) {
    this.requestDocumentEmitter.emit({
      parentRequestId: this.taskDetails.requestId,
      ...requestDocumentDetails,
    });
  }
}
